package so.libdvm.lora


import org.gradle.api.Plugin
import org.gradle.api.Project
import so.libdvm.lora.BuildInfoTask

class LoraPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println 'apply lora'
        BuildInfoTask buildInfoTask = project.task('lora', type: BuildInfoTask)
        project.afterEvaluate {
            project.tasks.findAll { task ->
                task.name.startsWith('generate') && task.name.endsWith('Resources')
            }.each { t ->
                t.dependsOn buildInfoTask
            }
        }
    }
}