package io.github.springstudent.dekstop.client.concurrent;


import io.github.springstudent.dekstop.common.log.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public abstract class Executable extends RunnableEx {
	private final ExecutorService executor;

	/**
	 * Limiting access to an unbounded queue (!)
	 */
	private final Semaphore semaphore;

	protected Executable(ExecutorService executor) {
		this.executor = executor;
		this.semaphore = null;
	}

	protected Executable(ExecutorService executor, Semaphore semaphore) {
		this.executor = executor;
		this.semaphore = semaphore;
	}

	@Override
    public final void doRun() throws IOException, InterruptedException {
		try {
			if (semaphore != null) {
				semaphore.release();
			}

			execute();
		} catch (InterruptedException ex) {
			if (!executor.isShutdown()) // executor.shutdownNow() ...
			{
				throw ex;
			}

			Log.info(Thread.currentThread().getName() + " has cancelled a task (shutdown)!");
		}
	}

	protected abstract void execute() throws IOException, InterruptedException;
}