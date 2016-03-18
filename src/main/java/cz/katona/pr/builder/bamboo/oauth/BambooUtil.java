package cz.katona.pr.builder.bamboo.oauth;

import cz.katona.pr.builder.bamboo.BambooException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.IOException;

class BambooUtil {

    private BambooUtil() {
    }

    public static String readPrivateKey(Resource privateKeyResource)  {
        try {
            String privateKeyString = IOUtils.toString(privateKeyResource.getInputStream());
            //we need to remove start and end of the key, because PKCS8EncodedKeySpec needs it
            return privateKeyString.replace("-----BEGIN PRIVATE KEY-----\n", "")
                    .replace("-----END PRIVATE KEY-----\n", "").replaceAll("\\s","");
        } catch (IOException e) {
            throw new BambooException("Can't read private key!", e);
        }

    }
}
