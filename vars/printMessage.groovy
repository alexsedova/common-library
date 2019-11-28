def call(Map params = [:]) {
    def message = params.get('message', '')
    String test = 'test'
    return message + ' ' + test
}