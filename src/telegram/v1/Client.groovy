package telegram.v1

class Client implements Serializable {

    def steps

    Client(steps) { this.steps = steps }

    def formatStatus(boolean status) {
        return status ? 'YES' : 'SKIP'
    }

    def emojiStatus(success, ci = false, cd = false) {
        if (!success) return '❌'

        def msg = '✅'

        if (!ci) {
            msg += ' ⏭️'
            return msg
        }

        msg += ' 📦'

        if (ci && cd) msg += ' 🖥️'

        return msg
    }

    def send(String message) {
        def encodedMessage = URLEncoder.encode(message, 'UTF-8')

        this.steps.withCredentials([steps.usernamePassword(credentialsId: 'telegram_chat_credentials', passwordVariable: 'TOKEN', usernameVariable: 'CHAT_ID')]) {
                return this.steps.httpRequest (
                        consoleLogResponseBody: true,
                        contentType: 'APPLICATION_JSON',
                        httpMode: 'GET',
                        url: "https://api.telegram.org/bot${this.steps.env.TOKEN}/sendMessage?text=$encodedMessage&chat_id=${this.steps.env.CHAT_ID}&disable_web_page_preview=true&parse_mode=HTML",
                        validResponseCodes: '200'
                )
        }
    }

    def success(args) {
        return this.send("""${emojiStatus(true, args.ci, args.cd)}<strong>CI/CD Success</strong>
-----------------------------
<em>${args.message}</em>

App Version: <pre>${args.appVersion}</pre>
Commit: <pre>${args.commitRef}</pre>
CI: <pre>${formatStatus(args.ci)}</pre>
CD: <pre>${formatStatus(args.cd)}</pre>

Link: <a href="${args.appLink}">${args.appLink}</a>""")
    }

    def fail(args) {
        return this.send("""${emojiStatus(false)}<strong>CI/CD Fail</strong>
-----------------------------

<a href="${args.logLink}">Find the log here</a>""")
    }

}
