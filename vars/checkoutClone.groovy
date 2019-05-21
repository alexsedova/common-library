def call(Map parameters = [:]) {
    checkout ([$class: 'GitSCM',
               branches: [[name: parameters.get('branchName', '*/**')]],
               doGenerateSubmoduleConfigurations: scm.doGenerateSubmoduleConfigurations,
               extensions: [[$class: 'LocalBranch'], [$class: 'CloneOption', noTags: false, shallow: false, depth: 0, reference: '']],
               userRemoteConfigs: getCustomScmPath(parameters)
    ])
}

def getCustomScmPath(Map parameters = [:]) {
    if (parameters.containsKey('vcsPath') && parameters.containsKey('credentialsId')) {
        return [[url: parameters.get('vcsPath'), credentialsId: parameters.get('credentialsId')]]
    }
    return scm.userRemoteConfigs
}