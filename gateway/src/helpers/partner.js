import axios from 'axios';
import jwtSimple from 'jwt-simple';

export const getRequestHeaders = async partnerConfig => {
  if (partnerConfig && partnerConfig.auth) {
    const { auth } = partnerConfig;
    if (auth.mechanism === 'jwt-secret') {
      const now = Math.round(Date.now() / 1000);
      const jwtToken = jwtSimple.encode(
        {
          iat: now,
          exp: now + 6000,
          ...auth.jwtPayload,
        },
        auth.secretKey,
        auth.algo || 'HS256',
      );
      const authRes = await axios.post(auth.url, auth.payload || {}, {
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });
      let authToken = '';
      let current = authRes.data;
      const path = auth.tokenPath.split('.');
      path.forEach(currentPath => {
        current = current[currentPath];
      });
      authToken = current;
      return {
        Authorization: `Bearer ${authToken}`,
      };
    }
    return {};
  }
  return partnerConfig.submit;
};

export const getPayload = (partnerConfig, payload) => {
  if (partnerConfig && partnerConfig.submit) {
    const { submit } = partnerConfig;
    if (submit.payload) {
      const finalPayload = {};
      const payloadKeys = Object.keys(submit.payload);
      payloadKeys.forEach(key => {
        let value = submit.payload[key];
        Object.keys(payload).forEach(keyWithValue => {
          value = value.replace(`\${${keyWithValue}}`, payload[keyWithValue]);
        });
        finalPayload[key] = value;
      });
      return finalPayload;
    }
  }
  return payload;
};
