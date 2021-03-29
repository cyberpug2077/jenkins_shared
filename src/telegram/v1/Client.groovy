package telegram.v1

class Client {

    private static String formatStatus(boolean status) {
        return status ? 'YES' : 'SKIP'
    }

    public static String send(String message) {
        def encodedMessage = URLEncoder.encode(message, 'UTF-8')

        withCredentials([usernamePassword(credentialsId: 'telegram_chat_credentials', passwordVariable: 'TOKEN', usernameVariable: 'CHAT_ID')]) {
                response = httpRequest (
                        consoleLogResponseBody: true,
                        contentType: 'APPLICATION_JSON',
                        httpMode: 'GET',
                        url: "https://api.telegram.org/bot$TOKEN/sendMessage?text=$encodedMessage&chat_id=$CHAT_ID&disable_web_page_preview=true",
                        validResponseCodes: '200'
                )

                return response
        }
    }

    public static String success(String message, String appVersion, String commitRef, String appLink, boolean ci, boolean cd) {
        return send("""<strong>CI/CD Success</strong>
-----------------------------
<em>${message}</em>

App Version: <pre>${appVersion}</pre>
Commit: <pre>${commitRef}</pre>
CI: <pre>${formatStatus(ci)}</pre>
CD: <pre>${formatStatus(cd)}</pre>

Link: <a href="${appLink}">${appLink}</a>""")
    }

}
