package jenkins.utils

class Validation {
    final static tools = ['mvn', 'gradle']
    def validateBuildTool(toolName) {
        if (!tools.contains(toolName)) {
            throw new RuntimeException("No such tool: ${toolName} is available")
        }
    }
}
