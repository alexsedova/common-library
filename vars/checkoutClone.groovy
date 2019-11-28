def call(Map params = [:]) {
    checkout ([$class: 'GitSCM',
               branches: [[name: params.get('branchName', '*/**')]],
               doGenerateSubmoduleConfigurations: scm.doGenerateSubmoduleConfigurations,
               extensions: [[$class: 'LocalBranch'], [$class: 'CloneOption', noTags: false, shallow: false, depth: 0, reference: '']],
               userRemoteConfigs: getCustomScmPath(params)
    ])
}

def getCustomScmPath(Map params = [:]) {
    if (params.containsKey('vcsPath') && params.containsKey('credentialsId')) {
        return [[url: params.get('vcsPath'), credentialsId: params.get('credentialsId')]]
    }
    return scm.userRemoteConfigs
}