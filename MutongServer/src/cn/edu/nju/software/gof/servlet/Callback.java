package cn.edu.nju.software.gof.servlet;

import java.io.IOException;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

import cn.edu.nju.software.gof.beans.oauth.OAuthRequest;
import cn.edu.nju.software.gof.beans.oauth.OAuthRequestIdentity;
import cn.edu.nju.software.gof.entity.EMF;
import cn.edu.nju.software.gof.entity.OAuthAccessKey;
import cn.edu.nju.software.gof.global.OAuthRequestTable;
import cn.edu.nju.software.gof.type.SynchronizationProvider;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class Callback extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGetAndPost(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGetAndPost(req, resp);
	}

	private void doGetAndPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String oauthVerifier = req.getParameter("oauth_verifier");
		String string_PersonID = req.getParameter("person_id");
		String string_ProviderType = req.getParameter("provider");
		//
		Key personID = KeyFactory.stringToKey(string_PersonID);
		SynchronizationProvider providerType = SynchronizationProvider
				.valueOf(string_ProviderType);
		OAuthRequestIdentity oauthRequestIdentity = new OAuthRequestIdentity(
				personID, providerType);
		//
		Map<OAuthRequestIdentity, OAuthRequest> requests = OAuthRequestTable
				.getOAuthRequests();
		OAuthProvider provider = null;
		OAuthConsumer consumer = null;
		synchronized (requests) {
			OAuthRequest request = requests.get(oauthRequestIdentity);
			consumer = request.getConsumer();
			provider = request.getProvider();
			requests.remove(oauthRequestIdentity);
		}
		if (provider == null) {
			return;
		}
		//
		try {
			provider.setOAuth10a(true);
			provider.retrieveAccessToken(consumer, oauthVerifier);
			//
			OAuthAccessKey key = new OAuthAccessKey(personID,
					consumer.getToken(), consumer.getTokenSecret(),
					providerType);
			EntityManager em = EMF.getInstance().createEntityManager();
			try {
				em.persist(key);
			} finally {
				em.close();
			}
		} catch (Exception exception) {
		}
	}
}
