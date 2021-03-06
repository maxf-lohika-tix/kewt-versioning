plugins {
    `java-gradle-plugin`
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("com.gradle.plugin-publish") version "0.11.0"
    id("maven-publish")
    id("com.github.mfarsikov.kewt-versioning") version "0.6.0"
}

group = "com.github.mfarsikov.kewt-versioning"
version = kewtVersioning.version

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.7.0.202003110725-r")

    runtimeOnly("org.jetbrains.kotlin:kotlin-script-runtime")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    implementation(kotlin("script-runtime"))
}

gradlePlugin {
    val kewtVersioning by plugins.creating {
        id = "com.github.mfarsikov.kewt-versioning"
        displayName = "Kewt versioning"
        implementationClass = "com.github.mfarsikov.kewt.versioning.plugin.KewtVersioningPlugin"
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations.getByName("functionalTestImplementation").extendsFrom(configurations.getByName("testImplementation"))

val functionalTest by tasks.creating(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

val check by tasks.getting(Task::class) {
    dependsOn(functionalTest)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

pluginBundle {
    website = "https://github.com/mfarsikov/kewt-versioning"
    vcsUrl = "https://github.com/mfarsikov/kewt-versioning"
    description = "Gradle plugin for versioning using Git tags"
    tags = listOf("git", "versioning")
}