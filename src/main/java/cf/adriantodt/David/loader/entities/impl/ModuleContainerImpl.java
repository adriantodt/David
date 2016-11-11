/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 19:22]
 */

package cf.adriantodt.David.loader.entities.impl;

import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.Type;
import cf.adriantodt.David.loader.entities.ModuleContainer;
import cf.adriantodt.David.loader.entities.ModuleResourceManager;

public class ModuleContainerImpl implements ModuleContainer {


	private final Class<?> moduleClass;
	private final Object moduleInstance;
	private final Module module;
	private final ModuleResourceManager manager;

	public ModuleContainerImpl(Module module, Class<?> moduleClass, Object moduleInstance, ModuleResourceManager manager) {
		this.module = module;
		this.moduleClass = moduleClass;
		this.moduleInstance = moduleInstance;
		this.manager = manager;
	}

	@Override
	public Class<?> getModuleClass() {
		return moduleClass;
	}

	@Override
	public Object getInstance() {
		return moduleInstance;
	}

	@Override
	public String getName() {
		return module.name();
	}

	@Override
	public Type[] getType() {
		return module.type();
	}

	@Override
	public ModuleResourceManager getResourceManager() {
		return manager;
	}
}
