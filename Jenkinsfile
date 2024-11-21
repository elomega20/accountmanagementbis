def ENV_NAME = getEnvName(env.BRANCH_NAME)
def CONTAINER_NAME = "accountmanagement-" + ENV_NAME
def CONTAINER_TAG = getTag(env.BUILD_NUMBER, env.BRANCH_NAME)
def HTTP_PORT = getHTTPPort(env.BRANCH_NAME)
def EMAIL_RECIPIENTS = "el1@yopmail.com,el2@yopmail.com"


node {
    try {
        stage('Initialize') {
            def dockerHome = tool 'DockerLatest'
            def mavenHome = tool 'MavenLatest'
            env.PATH = "${dockerHome}/bin:${mavenHome}/bin:${env.PATH}"
        }

        stage('Checkout') {
            checkout scm
        }

        stage('Build with test') {

            sh "mvn clean install -Dintegration-tests.skip=true -Dmaven.test.failure.ignore=true"
        }

        stage('Sonarqube Analysis') {
            withSonarQubeEnv('SonarQubeLocalServer') {
                sh "mvn sonar:sonar -Dsonar.report.export.path=target/sonar-report.json -Dintegration-tests.skip=true -Dmaven.test.failure.ignore=true"
            }
            timeout(time: 5, unit: 'MINUTES') {
                def qg = waitForQualityGate()
                if (qg.status != 'OK') {
                    error "Pipeline arrêté à cause de l'échec de la Quality Gate : ${qg.status}"
                }
            }
        }

        stage('Archive Report') {
            archiveArtifacts artifacts: 'target/sonar-report.json', allowEmptyArchive: true
        }

//         stage('Sonarqube Analysis') {
//             withSonarQubeEnv('SonarQubeLocalServer') {
//                 sh " mvn sonar:sonar -Dintegration-tests.skip=true -Dmaven.test.failure.ignore=true"
//             }
//             timeout(time: 1, unit: 'MINUTES') {
//                 def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
//                 if (qg.status != 'OK') {
//                     error "Pipeline aborted due to quality gate failure: ${qg.status}"
//                 }
//             }
//         }

        stage("Image Prune") {
            imagePrune(CONTAINER_NAME)
        }

        stage('Image Build') {
            imageBuild(CONTAINER_NAME, CONTAINER_TAG)
        }

        stage('Push to Docker Registry') {
            withCredentials([usernamePassword(credentialsId: 'dockerhubcredentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                pushToImage(CONTAINER_NAME, CONTAINER_TAG, USERNAME, PASSWORD)
            }
        }

    } finally {
        deleteDir()
        sendEmail(EMAIL_RECIPIENTS);
    }

}

def imagePrune(containerName) {
    try {
        sh "docker image prune -f"
        sh "docker stop $containerName"
    } catch (ignored) {
    }
}

def imageBuild(containerName, tag) {
    sh "docker build -t $containerName:$tag --pull --no-cache ."
    echo "Image build complete"
}

def pushToImage(containerName, tag, dockerUser, dockerPassword) {
    sh "docker login -u $dockerUser -p $dockerPassword"
    sh "docker tag $containerName:$tag $dockerUser/$containerName:$tag"
    sh "docker push $dockerUser/$containerName:$tag"
    echo "Image push complete"
}

def sendEmail(recipients) {
    def buildStatus = currentBuild.currentResult ?: 'SUCCESS'
    def buildMessage = buildStatus == 'SUCCESS' ? 'Le Build a réussi!' : 'Le Build a échoué.'

    if (fileExists('target/sonar-report.json')) {
        mail(
            to: recipients,
            subject: "Build ${env.BUILD_NUMBER} - ${buildStatus} - (${currentBuild.fullDisplayName})",
            body: "Bonjour,\n\n${buildMessage}\n\nConsultez le rapport SonarQube joint pour plus de détails.\n",
            attachments: 'target/sonar-report.json'
        )
    } else {
        mail(
            to: recipients,
            subject: "Build ${env.BUILD_NUMBER} - ${buildStatus} - (${currentBuild.fullDisplayName})",
            body: "Bonjour,\n\n${buildMessage}\n\nLe rapport SonarQube n'a pas été généré.\n"
        )
    }
}

// def sendEmail(recipients) {
//     mail(
//             to: recipients,
//             subject: "Build ${env.BUILD_NUMBER} - ${currentBuild.currentResult} - (${currentBuild.fullDisplayName})",
//             body: "Hello Teams+"+"\n" +"Le Build a reussie!"+ "\n")
// }

String getEnvName(String branchName) {
    if (branchName == 'main') {
        return 'prod'
    }
    return (branchName == 'develop') ? 'uat' : 'dev'
}

String getHTTPPort(String branchName) {
    if (branchName == 'main') {
        return '8086'
    }
    return (branchName == 'develop') ? '8085' : '8084'
}

String getTag(String buildNumber, String branchName) {
    if (branchName == 'main') {
        return buildNumber + '-stable'
    }
    return buildNumber + '-unstable'
}
