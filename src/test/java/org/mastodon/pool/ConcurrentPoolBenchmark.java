/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.pool;

import net.imglib2.parallel.TaskExecutors;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This benchmark compares the performance of {@link ConcurrentLinkedQueue}
 * against {@link ThreadLocalSoftReferencePool}.
 */

@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
@Warmup( iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS )
@State( Scope.Benchmark )
public class ConcurrentPoolBenchmark
{

	@Param({"false", "true"})
	public boolean multiThreaded;

	@Benchmark
	public void benchmarkNoPool()
	{
		runBenchmark( () -> null, ignore -> {});
	}

	@Benchmark
	public void benchmarkConcurrentLinkedQueue()
	{
		Queue<Object> queue = new ConcurrentLinkedQueue<>();
		runBenchmark( queue::poll, queue::add );
	}

	@Benchmark
	public void benchmarkThreadLocalSoftReferencePool()
	{
		final ThreadLocalSoftReferencePool<Object> pool = new ThreadLocalSoftReferencePool<>();
		runBenchmark( pool::get, pool::put );
	}

	private void runBenchmark( Supplier<Object> get, Consumer<Object> put )
	{
		if(multiThreaded)
			multiThreadedBenchmark( get, put );
		else
			runBenchmarkTask( get, put );
	}

	private void multiThreadedBenchmark( Supplier<Object> get, Consumer<Object> put )
	{
		List<Integer> indices = IntStream.range( 0, 8 ).boxed().collect( Collectors.toList() );
		TaskExecutors.multiThreaded().forEach( indices,
				ignore -> runBenchmarkTask( get, put )
		);
	}

	private void runBenchmarkTask( Supplier<Object> get, Consumer<Object> put )
	{
		for ( int i = 0; i < 1000; i++ )
			runRecursive( get, put, 1000 );
	}

	private static void runRecursive( Supplier<Object> get, Consumer<Object> put, final int value )
	{
		if( value < 0)
			return;

		// get SimpleObject from pool
		SimpleObject object = ( SimpleObject ) get.get();
		if(object == null)
			object = new SimpleObject();

		// set value
		object.set( value );

		runRecursive( get, put, value - 1 );

		// get value and make sure it didn't change
		if(object.get() != value )
			throw new AssertionError("The pool seems to be broken");

		// return SimpleObject to pool
		put.accept( object );
	}

	private static class SimpleObject
	{

		// NB: Having an array here means that creating this "SimpleObject" will
		// take a little longer. How large this array is, affects the benchmark
		// results. When the array is small, than using no poll is the fastest
		// approach.
		private final int[] values = new int[20];

		public int get() {
			return values[0];
		}

		public void set(int value) {
			values[0] = value;
		}
	}

	public static void main(String... args) throws RunnerException
	{
		Options options = new OptionsBuilder().include( ConcurrentPoolBenchmark.class.getName() ).build();
		new Runner( options ).run();
	}

}
