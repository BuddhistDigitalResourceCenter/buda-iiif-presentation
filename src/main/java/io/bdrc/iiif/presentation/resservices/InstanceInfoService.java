package io.bdrc.iiif.presentation.resservices;

import static io.bdrc.iiif.presentation.AppConstants.CACHEPREFIX_WI;
import static io.bdrc.iiif.presentation.AppConstants.GENERIC_APP_ERROR_CODE;
import static io.bdrc.iiif.presentation.AppConstants.GENERIC_LDS_ERROR;
import static io.bdrc.iiif.presentation.AppConstants.LDS_WORKGRAPH_QUERY;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bdrc.iiif.presentation.exceptions.BDRCAPIException;
import io.bdrc.iiif.presentation.resmodels.InstanceInfo;

public class InstanceInfoService extends ConcurrentResourceService<InstanceInfo> {
	private static final Logger logger = LoggerFactory.getLogger(InstanceInfoService.class);

	public static final InstanceInfoService Instance = new InstanceInfoService();

	InstanceInfoService() {
		super(CACHEPREFIX_WI);
	}

	final public InstanceInfo getFromApi(final String workId) throws BDRCAPIException {
		logger.debug("fetch workInfo on LDS for {}", workId);
		final HttpClient httpClient = HttpClientBuilder.create().build(); // Use this instead
		final Model resModel;
		final String queryUrl = LDS_WORKGRAPH_QUERY;
		logger.debug("query {} with argument R_RES={}", queryUrl, workId);
		try {
			final HttpPost request = new HttpPost(queryUrl);
			// we suppose that the volumeId is well formed, which is checked by the
			// Identifier constructor
			final StringEntity params = new StringEntity("{\"R_RES\":\"" + workId + "\"}", ContentType.APPLICATION_JSON);
			request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
			request.setEntity(params);
			final HttpResponse response = httpClient.execute(request);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				// should indicate the actual LDS http code (mostly 404 instead of 500)
				throw new BDRCAPIException(code, GENERIC_LDS_ERROR, "LDS lookup returned http code " + code, response.toString(), "");
			}
			final InputStream body = response.getEntity().getContent();
			resModel = ModelFactory.createDefaultModel();
			// TODO: prefixes
			resModel.read(body, null, "TURTLE");
		} catch (IOException ex) {
			throw new BDRCAPIException(500, GENERIC_APP_ERROR_CODE, ex);
		}
		logger.debug("found workModel: {}", resModel);
		return new InstanceInfo(resModel, workId);
	}

}