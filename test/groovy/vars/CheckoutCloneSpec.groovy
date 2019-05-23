import com.praqma.jenkins.utilities.PipelineTestRunner
import spock.lang.Specification

class CheckoutCloneSpec extends Specification {
    PipelineTestRunner runner
    def testData
    String stepScriptPath = 'vars/checkoutClone.groovy'

    def setup() {
        testData = [:]
        runner = new PipelineTestRunner()
    }

    def 'verify script loading correct entries'() {
        given:
        def stepScript = runner.load {
            script stepScriptPath
            method 'checkout', [Map.class], {
                map ->
                    testData['$class'] = map['$class']
                    testData['branches'] = map['branches']
                    testData['extensions'] = map['extensions']
            }
        }

        when:
        stepScript(['branchName': 'integration/bla'])

        then:
        testData.$class == 'GitSCM'
        testData.branches[0].name == 'integration/bla'
        testData.extensions[0].$class == 'LocalBranch'
        testData.extensions[1].$class == 'CloneOption'
        testData.extensions[1].noTags == false
    }

    def 'check if returns custom vsc path'() {
        given:
        def stepScript = runner.load {
            script stepScriptPath
            method 'checkout', [Map.class], {
                map ->
                    testData['userRemoteConfigs'] = map['userRemoteConfigs']
            }
        }

        when:
        stepScript([vcsPath: 'ssh://test@project.git', credentialsId: 'userCredentials'])

        then:
        testData.userRemoteConfigs == [[url: 'ssh://test@project.git', credentialsId: 'userCredentials']]
    }

    def 'check if returns scm default config if no custom path provided'() {
        given:
        def stepScript = runner.load {
            script stepScriptPath
            method 'checkout', [Map.class], {
                map ->
                    testData['userRemoteConfigs'] = map['userRemoteConfigs']
            }
            property 'scm', [userRemoteConfigs: 'test']
        }

        when:
        stepScript()

        then:
        testData.userRemoteConfigs == 'test'
    }
}