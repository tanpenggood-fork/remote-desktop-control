package io.github.springstudent.dekstop.common.configuration;

public interface Configurable<T extends Configuration> {
	/**
	 * Initial configuration.
	 */
	void configure(T configuration);
}
