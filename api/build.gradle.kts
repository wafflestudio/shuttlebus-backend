dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // jsoup to parse XML
    api(group = "org.jsoup", name = "jsoup", version = "1.10.2")
    api(group = "org.json", name = "json", version = "20180813")
}
