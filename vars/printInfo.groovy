def call(Map config) {
    def name = config.get('name', '')
    return "Bla on ${name}"
}