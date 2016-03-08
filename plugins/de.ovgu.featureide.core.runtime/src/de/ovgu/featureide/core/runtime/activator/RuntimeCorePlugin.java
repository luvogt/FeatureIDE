package de.ovgu.featureide.core.runtime.activator;

import org.osgi.framework.BundleContext;
import de.ovgu.featureide.fm.core.AbstractCorePlugin;

/**
 * Images plugin
 * 
 * @author Jabier Martinez
 *
 */
public class RuntimeCorePlugin extends AbstractCorePlugin {

	public static final String PLUGIN_ID = "de.ovgu.featureide.core.runtime";

	@Override
	public String getID() {
		return PLUGIN_ID;
	}

	private static RuntimeCorePlugin plugin;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static RuntimeCorePlugin getDefault() {
		return plugin;
	}

}
