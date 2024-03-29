package com.fuzzy.subsystems.subsystem;

import com.infomaximum.cluster.struct.Component;
import com.infomaximum.platform.sdk.component.version.CompatibleVersion;
import com.infomaximum.platform.sdk.component.version.Version;
import com.fuzzy.subsystems.utils.ManifestUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

public class Info extends com.infomaximum.platform.sdk.component.Info {

	public static class DependenceVersion {
		public final Class<? extends Subsystem> subsystem;
		public final CompatibleVersion version;

		private DependenceVersion(Class<? extends Subsystem> subsystem, CompatibleVersion version) {
			this.subsystem = subsystem;
			this.version = version;
		}
	}

	private static final String JSON_PROP_NAME = ManifestUtils.JSON_PROP_NAME;
	private static final String JSON_PROP_DISPLAY_NAME = ManifestUtils.JSON_PROP_DISPLAY_NAME;
	private static final String JSON_PROP_VERSION = ManifestUtils.JSON_PROP_VERSION;
	private static final String JSON_PROP_SDK = "sdk";
	private static final String JSON_PROP_DEPENDENCIES = "dependencies";
	private static final String JSON_PROP_UUID = "uuid";
	private static final String JSON_PROP_MINIMUM = "minimum";
	private static final String JSON_PROP_TARGET = "target";

	private final boolean platform;
	private final String name;
	private final String displayName;
	private final CompatibleVersion sdkVersion;
	private final List<DependenceVersion> dependenceVersions;

	private Info(Builder builder) {
		super(builder);

		this.platform = builder.platform;
		this.name = StringUtils.isEmpty(builder.name) ? getComponent().getSimpleName() : builder.name;
		this.displayName = StringUtils.isEmpty(builder.displayName) ? this.name : builder.displayName;
		this.sdkVersion = builder.sdkVersion;
		this.dependenceVersions = Collections.unmodifiableList(builder.dependenceVersions.entrySet().stream()
				.map(entry -> new DependenceVersion(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList()));
	}

	public boolean isPlatform() {
		return platform;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public CompatibleVersion getSdkVersion() {
		return sdkVersion;
	}

	public List<DependenceVersion> getDependenceVersions() {
		return dependenceVersions;
	}

	@Override
	public Class<? extends Subsystem> getComponent() {
		return (Class<? extends Subsystem>) super.getComponent();
	}

	public static class Builder extends com.infomaximum.platform.sdk.component.Info.Builder<Builder> {

		private boolean platform = false;
		private String name;
		private String displayName;
		private CompatibleVersion sdkVersion;
		private Map<Class<? extends Subsystem>, CompatibleVersion> dependenceVersions = new HashMap<>();

		public Builder(String uuid, Version version) {
			super(uuid, version);
		}

		@Override
		public Builder withComponentClass(Class<? extends Component> componentClass) {
			if (!Subsystem.class.isAssignableFrom(componentClass)) {
				throw new IllegalArgumentException();
			}
			return super.withComponentClass(componentClass);
		}

		public Builder withPlatform(boolean platform) {
			this.platform = platform;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withDisplayName(String displayName) {
			this.displayName = displayName;
			return this;
		}

		public Builder withSdkVersion(CompatibleVersion version) {
			this.sdkVersion = version;
			return this;
		}

		@Override
		public Builder withDependence(Class<? extends Component> dependence) {
			if (Subsystem.class.isAssignableFrom(dependence)) {
				throw new IllegalArgumentException();
			}
			return super.withDependence(dependence);
		}

		public Builder withDependence(Class<? extends Subsystem> dependence, CompatibleVersion version) {
			super.withDependence(dependence);
			dependenceVersions.put(dependence, version);
			return this;
		}

		@Override
		public Info build() {
			return new Info(this);
		}

		private void readJson(JSONObject manifest) throws ParseException, ReflectiveOperationException {
			withPlatform(false);
			withName(manifest.getAsString(JSON_PROP_NAME));
			withDisplayName(manifest.getAsString(JSON_PROP_DISPLAY_NAME));
			withComponentClass(toClass(uuid));

			JSONObject sdkObject = (JSONObject) manifest.get(JSON_PROP_SDK);
			withSdkVersion(toCompatibleVersion((JSONObject) sdkObject.get(JSON_PROP_VERSION)));
			JSONArray dependencies = (JSONArray) sdkObject.get(JSON_PROP_DEPENDENCIES);
			if (dependencies != null) {
				for (Object dependence : dependencies) {
					JSONObject dep = (JSONObject) dependence;
					withDependence(toClass(dep.getAsString(JSON_PROP_UUID)), sdkVersion);
				}
			}

			dependencies = (JSONArray) manifest.get(JSON_PROP_DEPENDENCIES);
			if (dependencies != null) {
				for (Object dependence : dependencies) {
					JSONObject dep = (JSONObject) dependence;
					withDependence(toClass(dep.getAsString(JSON_PROP_UUID)), toCompatibleVersion((JSONObject) dep.get(JSON_PROP_VERSION)));
				}
			}
		}

		private static CompatibleVersion toCompatibleVersion(JSONObject object) throws ParseException {
			final Version target = Version.parse(object.getAsString(JSON_PROP_TARGET));
			final String minVer = object.getAsString(JSON_PROP_MINIMUM);
			if (minVer == null) {
				return new CompatibleVersion(target);
			}
			return new CompatibleVersion(Version.parse(minVer), target);
		}

		private static Class<? extends Subsystem> toClass(String uuid) throws ReflectiveOperationException {
			Set<Class<? extends Subsystem>> classes = new Reflections(uuid).getSubTypesOf(Subsystem.class);
			switch (classes.size()) {
				case 0:
					throw new ClassNotFoundException("Subsystem not found in " + uuid);
				case 1:
					return classes.iterator().next();
				default:
					throw new ReflectiveOperationException("Names is clashed: [" + StringUtils.join(classes, ", ") + "]");
			}
		}

		public static Builder fromManifest(String uuid) {
			try {
				JSONObject manifest = ManifestUtils.parseManifest(uuid);

				Version version = Version.parse(manifest.getAsString(JSON_PROP_VERSION));

				Builder builder = new Builder(uuid, version);
				builder.readJson(manifest);
				return builder;
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}
}
