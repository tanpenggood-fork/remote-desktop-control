package io.github.springstudent.dekstop.common.configuration;

public interface ReConfigurable<T extends Configuration> extends Configurable<T> {
	/**
	 * Allows for dynamic re-configuration.
	 */
	void reconfigure(T configuration);

}