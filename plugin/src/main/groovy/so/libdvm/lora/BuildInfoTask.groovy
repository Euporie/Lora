package so.libdvm.lora

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.tasks.AndroidVariantTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.os.OperatingSystem

import java.text.SimpleDateFormat

class BuildInfoTask extends AndroidVariantTask {

    private File buildInfoPath

    BuildInfoTask() {
        setVariantName("lora")
        buildInfoPath = createBuildPath()
    }

    File createBuildPath() {
        File path = new File(project.buildDir, 'generated/buildInfo/')
        if (project.plugins.hasPlugin(AppPlugin)) {
            println 'is in app module'
            project.extensions.getByType(AppExtension).sourceSets({
                main.resources.srcDirs += path
                println main.resources.srcDirs
            })
        } else if (project.plugins.hasPlugin(LibraryPlugin)) {
            println 'is in sdk module'
            project.extensions.getByType(LibraryExtension).sourceSets({
                main.resources.srcDirs += path
            })
        } else {
            println 'other situation'
        }
        return path
    }

    @TaskAction
    void createBuildInfo() {
        println 'start create build info'
        buildInfoPath.mkdirs()
        def infoFile = new File(buildInfoPath, 'lora.properties')
        StringBuilder sb = new StringBuilder();
        sb.append(getLoraVersion() + "\n")
        sb.append(getBuildTime() + "\n")
        sb.append(getBuildEnv() + "\n")
        sb.append(getBuildHostName() + "\n")
        sb.append(getBuildHostAddress() + "\n")
        sb.append(getGitURL() + "\n")
        sb.append(getGitBranch() + "\n")
        sb.append(getLastCommit() + "\n")
        sb.append(getGitTagDescribe() + "\n")
        sb.append(isJenkins() + "\n")
        sb.append(getJenkinsURL() + "\n")
        sb.append(getJenkinsBuildUser() + "\n")
        sb.append(getJenkinsJobName() + "\n")
        sb.append(getJenkinsBuildNumber() + "\n")
        infoFile.text = sb.toString()
        println 'finish'
    }


    String getLoraVersion() {
        return "Created-By:Lora:" + LoraPlugin.class.getPackage().getImplementationVersion()
    }

    String getBuildTime() {
        return "BuildTime=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())
    }

    String isJenkins() {
        return "isInJenkins=" + isInJenkins()
    }

    boolean isInJenkins() {
        Map<String, String> environmentMap = System.getenv()
        boolean result = false
        if (environmentMap != null
                && environmentMap.containsKey("JOB_NAME")
                && environmentMap.containsKey("BUILD_NUMBER")) {
            result = true
        }
        return result
    }

    String getGitTagDescribe() {
        return 'TagDescribe=' + 'git describe --tags'.execute([], project.rootDir).text.trim()
    }

    String getJenkinsURL() {
        String result = "JenkinsURL="
        if (isInJenkins()) {
            result = result + System.getenv().JOB_URL
        }
        return result
    }

    String getJenkinsJobName() {
        String result = "JenkinsJobName="
        if (isInJenkins()) {
            result = result + System.getenv().JOB_NAME
        }
        return result
    }

    String getJenkinsBuildNumber() {
        String result = "JenkinsBuildNumber="
        if (isInJenkins()) {
            ext.buildNumber = System.getenv().BUILD_NUMBER?.toInteger()
            result = result + "$buildNumber"
        }
        return result
    }

    String getGitBranch() {
        String result = "Branch="
        if (isInJenkins()) {
            result = result + System.getenv().GIT_BRANCH
        } else {
            result = result + 'git symbolic-ref --short -q HEAD'.execute().text.trim()
        }
        return result
    }

    String getGitURL() {
        return "GitURL=" + 'git remote -v'.execute().text.trim()
    }

    String getBuildEnv() {
        return "BuildEnv=" + OperatingSystem.current().toString()
    }

    String getLastCommit() {
        return "LastCommit=" + "git log  -1".execute().text.trim()
    }

    String getBuildHostName() {
        return "BuildHostName=" + InetAddress.getLocalHost().getHostName()
    }

    String getJenkinsBuildUser() {
        String result = "JenkinsBuildUser="
        if (isInJenkins()) {
            result = result + System.getenv().BUILD_USER
        }
        return result
    }

    String getBuildHostAddress() {
        return "BuildHostAddress=" + InetAddress.getLocalHost().getHostAddress()
    }
}