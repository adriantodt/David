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

import cf.adriantodt.David.loader.entities.ModuleContainer;

import java.lang.annotation.Annotation;

public class ModuleContainerImpl implements ModuleContainer {


	private final Class<?> moduleClass;
	private final Object moduleInstance;

	public ModuleContainerImpl(Class<?> moduleClass, Object moduleInstance) {
		this.moduleClass = moduleClass;
		this.moduleInstance = moduleInstance;
	}

	@Override
	public Class<?> getModuleClass() {
		return moduleClass;
	}

	@Override
	public Object getInstance() {
		return moduleInstance;
	}
}
