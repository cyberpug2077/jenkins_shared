package telegram.v1

class Client implements Serializable {

    def steps

    Client(steps) { this.steps = steps }

    def formatStatus(boolean status) {
        return status ? 'YES' : 'SKIP'
    }

    def send(String message) {
        def encodedMessage = URLEncoder.encode(message, 'UTF-8')

        this.steps.withCredentials([steps.usernamePassword(credentialsId: 'telegram_chat_credentials', passwordVariable: 'TOKEN', usernameVariable: 'CHAT_ID')]) {
                response = steps.httpRequest (
                        consoleLogResponseBody: true,
                        contentType: 'APPLICATION_JSON',
                        httpMode: 'GET',
                        url: 'https://api.telegram.org/bot${TOKEN}/sendMessage?text=${encodedMessage}&chat_id=${CHAT_ID}&disable_web_page_preview=true',
                        validResponseCodes: '200'
                )

            return response
        }
    }

    def success(args) {
        return this.send("""<strong>CI/CD Success</strong>
-----------------------------
<em>${args.message}</em>

App Version: <pre>${args.appVersion}</pre>
Commit: <pre>${args.commitRef}</pre>
CI: <pre>${formatStatus(args.ci)}</pre>
CD: <pre>${formatStatus(args.cd)}</pre>

Link: <a href="${args.appLink}">${args.appLink}</a>""")
    }

}
