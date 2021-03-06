package site.zido.elise.downloader;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.zido.elise.Page;
import site.zido.elise.Request;
import site.zido.elise.Task;
import site.zido.elise.proxy.Proxy;
import site.zido.elise.proxy.ProxyProvider;
import site.zido.elise.select.HTML;
import site.zido.elise.select.Text;
import site.zido.elise.utils.ValidateUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * HtmlUnitDownloader
 *
 * @author zido
 */
public class HtmlUnitDownloader extends AbstractDownloader {
    private static final Logger logger = LoggerFactory.getLogger(HtmlUnitDownloader.class);
    private ProxyProvider proxyProvider;

    @Override
    public Page download(Request request, Task task) {
        WebClient webClient = null;
        Proxy proxy = proxyProvider != null ? proxyProvider.getProxy(task) : null;
        Page page = Page.fail();
        try {
            if (proxy != null) {
                webClient = new WebClient(BrowserVersion.CHROME, proxy.getHost(), proxy.getPort());
                if (!ValidateUtils.isEmpty(proxy.getUsername()) && !ValidateUtils.isEmpty(proxy.getPassword())) {
                    webClient.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword()));
                }
            } else {
                webClient = new WebClient(BrowserVersion.CHROME);
            }
            String urlStr = request.getUrl();
            URL url = new URL(urlStr);

            WebRequest webRequest = new WebRequest(url);
            String charset = request.getCharset();
            if (!ValidateUtils.isEmpty(charset)) {
                webRequest.setCharset(Charset.forName(charset));
            }
            HtmlPage htmlPage = webClient.getPage(webRequest);
            int statusCode = htmlPage.getWebResponse().getStatusCode();
            page = new Page();
            page.setStatusCode(statusCode);
            page.setUrl(new Text(request.getUrl()));
            page.setBody(new HTML(htmlPage.asXml(), htmlPage.getUrl().toString()));
            page.setDownloadSuccess(true);
        } catch (MalformedURLException e) {
            logger.error(String.format("url is invalid [%s]", request.getUrl()), e);
        } catch (IOException e) {
            logger.error(String.format("download page fail [%s]", request.getUrl()), e);
        } finally {
            if (webClient != null) {
                webClient.close();
            }
        }
        return page;
    }

    @Override
    public void setThread(int threadNum) {

    }

    public HtmlUnitDownloader setProxyProvider(ProxyProvider proxyProvider) {
        this.proxyProvider = proxyProvider;
        return this;
    }
}
