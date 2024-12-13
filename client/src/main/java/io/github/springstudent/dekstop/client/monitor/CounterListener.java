package io.github.springstudent.dekstop.client.monitor;


import io.github.springstudent.dekstop.client.bean.Listener;
public interface CounterListener<T> extends Listener {
	void onInstantValueUpdated(Counter<T> counter, T value);
}
