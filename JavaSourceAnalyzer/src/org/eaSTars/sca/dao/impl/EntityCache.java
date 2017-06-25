package org.eaSTars.sca.dao.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.dblayer.model.GenericModel;

public class EntityCache <K extends Object, V extends GenericModel> {
	
	private static final Logger LOGGER = LogManager.getLogger(EntityCache.class);
	
	private String name;
	
	@FunctionalInterface
	public interface EntitityProvider <T extends GenericModel> {
		public T getEntry();
	}
	
	private class CacheInfo {

		private long lastAccess;

		private V cachedObject;
		
		public CacheInfo(V instance) {
			cachedObject = instance;
			updateLastAccess();
		}

		public V getCachedObject() {
			return cachedObject;
		}
		
		public long getLastAccess() {
			return lastAccess;
		}
		
		public void updateLastAccess() {
			lastAccess = System.currentTimeMillis();
		}
	}
	
	private int cacheSize;
	
	private Map<K, CacheInfo> modelCache = new LinkedHashMap<K, CacheInfo>();
	
	private int cacheAttempt = 0;
	
	private int cacheHit = 0;
	
	public EntityCache(int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	public EntityCache(String name, int cacheSize) {
		this(cacheSize);
		this.name = name;
	}
	
	public V getItemFromCache (K key, EntitityProvider<V> entryprovider) {
		cacheAttempt++;
		
		CacheInfo cacheinfo = modelCache.get(key);
		V result = null;
		if (cacheinfo == null) {
				result = entryprovider.getEntry();
				if (result != null) {
					putInModelCache(key, result);
				}
		} else {
			cacheinfo.updateLastAccess();
			result = cacheinfo.getCachedObject();
			cacheHit++;
		}
		LOGGER.debug(() -> String.format("[%s-%s] hit ratio: %d / %d (%d%%)", this.getClass().getSimpleName(), name, cacheHit, cacheAttempt, (int)((double)cacheHit / cacheAttempt * 100)));
		
		return result;
	}
	
	public void putInModelCache(K key, V model) {
		if (modelCache.size() > cacheSize) {
			modelCache.entrySet().stream()
			.min((o1, o2) -> Long.compare(o1.getValue().getLastAccess(), o2.getValue().getLastAccess()))
			.ifPresent(e -> modelCache.remove(e.getKey()));
		}
		modelCache.put(key, new CacheInfo(model));
	}
}
