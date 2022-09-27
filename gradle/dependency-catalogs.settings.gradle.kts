dependencyResolutionManagement {
    versionCatalogs {
        create("coreLibs") {
            from(files("core.libs.versions.toml"))
        }
        create("androidLibs") {
            from(files("android.libs.versions.toml"))
        }
        create("testLibs") {
            from(files("test.libs.versions.toml"))
        }
    }
}