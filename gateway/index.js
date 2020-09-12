const CanvasApi = require('caccl-api');

const canvasApi = new CanvasApi({
  accessToken:
    '8Ap5oAGL0G7eIyhE9J9aKj3a2dla0osZnBgfckAr4RatNpyvQz4ij5pC93GOjKKL',
  canvasHost: 'canvas.docker',
  basePath: 'http://canvas.docker',
  // cacheType: config.cacheType,
  // cache: config.cache,
  // sendRequest: config.sendRequest,
  // numRetries: config.numRetries,
  // itemsPerPage: config.itemsPerPage,
});

canvasApi.user.self.getProfile().then(profile => {
  console.log('profile ==', profile);
});
