def call(Map config) {
    def name = config.get('name', '')
    return "I'm building on ${name}"
}