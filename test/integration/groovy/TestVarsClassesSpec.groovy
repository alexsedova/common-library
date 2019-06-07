import com.cloudbees.hudson.plugins.folder.computed.FolderComputation
import jenkins.branch.BranchProperty
import jenkins.branch.BranchSource
import jenkins.branch.DefaultBranchPropertyStrategy
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.LibraryRetriever
import com.mkobit.jenkins.pipelines.codegen.LocalLibraryRetriever
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
import jenkins.plugins.git.GitSCMSource
import jenkins.plugins.git.GitBranchSCMHead
import jenkins.plugins.git.GitSampleRepoRule
import jenkins.scm.api.SCMHead

import org.junit.Rule
import spock.lang.Specification
import org.jvnet.hudson.test.JenkinsRule
import static org.junit.Assert.*

import javax.annotation.Nonnull

/**
 *
 */
class TestVarsClassesSpec extends Specification {

    @Rule
    JenkinsRule j = new JenkinsRule()

    @Rule
    public GitSampleRepoRule sampleRepo = new GitSampleRepoRule()

    void setup() {
        j.timeout = 30
        final LibraryRetriever retriever = new LocalLibraryRetriever()
        final LibraryConfiguration localLibrary =
                new LibraryConfiguration('common-library', retriever)
        localLibrary.implicit = true
        localLibrary.defaultVersion = 'master'
        localLibrary.allowVersionOverride = true
        GlobalLibraries.get().setLibraries(Collections.singletonList(localLibrary))
    }

    void "run script from the same local library"() {
        when:
        CpsFlowDefinition flow = new CpsFlowDefinition('''
                import setBuildEnv
                setBuildEnv()'''.stripIndent(), true)
        WorkflowJob workflowJob = j.createProject(WorkflowJob, 'project')
        workflowJob.definition = flow

        then:
        WorkflowRun result = j.buildAndAssertSuccess(workflowJob)
        println result.log
        j.assertLogContains('here', result)
    }

    void "run script not from library"() {
        when:
        CpsFlowDefinition flow = new CpsFlowDefinition('''
                import setBuildEnv
                setBuildEnv()'''.stripIndent(), true)
        WorkflowJob workflowJob = j.createProject(WorkflowJob, 'project')
        workflowJob.definition = flow

        then:
        WorkflowRun result = j.buildAndAssertSuccess(workflowJob)
        println result.log
        j.assertLogContains('here', result)
    }

    // For testing checkout scm required miltibranch pipeline job
    void "run checkout and clone"() {
        given:
        sampleRepo.init()
        sampleRepo.write("Jenkinsfile", "echo \"branch=\${env.BRANCH_NAME}\"; node {checkoutClone([:])}")
        sampleRepo.write("file", "initial content")
        sampleRepo.git("add", "Jenkinsfile")
        sampleRepo.git("commit", "--all", "--message=flow")

        WorkflowMultiBranchProject mp = j.createProject(WorkflowMultiBranchProject, 'project')
        mp.getSourcesList().add(new BranchSource(new GitSCMSource(null, sampleRepo.toString(), "", "*", "", false), new DefaultBranchPropertyStrategy(new BranchProperty[0])));

        when:
        WorkflowJob p = scheduleAndFindBranchProject(mp, "master")

        then:
        assertEquals(new GitBranchSCMHead("master"), SCMHead.HeadByItem.findHead(p));
        assertEquals(1, mp.getItems().size());
        j.waitUntilNoActivity();
        WorkflowRun b1 = p.getLastBuild();
        assertEquals(1, b1.getNumber());
    }

    void "run test scm"() {
        given:
        sampleRepo.init()
        sampleRepo.write("Jenkinsfile", "echo \"branch=\${env.BRANCH_NAME}\"; node {checkout scm; echo readFile('file')}")
        sampleRepo.write("file", "initial content")
        sampleRepo.git("add", "Jenkinsfile")
        sampleRepo.git("commit", "--all", "--message=flow")

        WorkflowMultiBranchProject mp = j.createProject(WorkflowMultiBranchProject, 'project')
        mp.getSourcesList().add(new BranchSource(new GitSCMSource(null, sampleRepo.toString(), "", "*", "", false), new DefaultBranchPropertyStrategy(new BranchProperty[0])));

        when:
        WorkflowJob p = scheduleAndFindBranchProject(mp, "master")

        then:
        assertEquals(new GitBranchSCMHead("master"), SCMHead.HeadByItem.findHead(p));
        assertEquals(1, mp.getItems().size());
        j.waitUntilNoActivity();
        WorkflowRun b1 = p.getLastBuild();
        assertEquals(1, b1.getNumber());
        j.assertLogContains("initial content", b1);
        j.assertLogContains("branch=master", b1);
    }

    public static @Nonnull WorkflowJob scheduleAndFindBranchProject(@Nonnull WorkflowMultiBranchProject mp, @Nonnull String name) throws Exception {
        mp.scheduleBuild2(0).getFuture().get();
        return findBranchProject(mp, name);
    }

    public static @Nonnull WorkflowJob findBranchProject(@Nonnull WorkflowMultiBranchProject mp, @Nonnull String name) throws Exception {
        WorkflowJob p = mp.getItem(name);
        showIndexing(mp);
        if (p == null) {
            fail(name + " project not found");
        }
        return p;
    }

    static void showIndexing(@Nonnull WorkflowMultiBranchProject mp) throws Exception {
        FolderComputation<?> indexing = mp.getIndexing();
        System.out.println("---%<--- " + indexing.getUrl());
        indexing.writeWholeLogTo(System.out);
        System.out.println("---%<--- ");
    }
}
