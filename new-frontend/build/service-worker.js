/**
 * Welcome to your Workbox-powered service worker!
 *
 * You'll need to register this file in your web app and you should
 * disable HTTP caching for this file too.
 * See https://goo.gl/nhQhGp
 *
 * The rest of the code is auto-generated. Please don't update this file
 * directly; instead, make changes to your Workbox build configuration
 * and re-run your build process.
 * See https://goo.gl/2aRDsh
 */

importScripts("https://storage.googleapis.com/workbox-cdn/releases/4.3.1/workbox-sw.js");

importScripts(
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 81a65d3bf979f218ae8ce3684da07843bc4e2ea2
  "/precache-manifest.8bc93abf3c81906d63e7d7555f19ec53.js"
=======
  "/precache-manifest.229263784e401c5ec16fd9a713b84577.js"
>>>>>>> 01f7a6f30f3fa6eb9cfaafefea652a4ace7938e1
<<<<<<< HEAD
=======
  "/precache-manifest.8bc93abf3c81906d63e7d7555f19ec53.js"
>>>>>>> cd8747cb88a37fe877d2b1aab25f196154a813d7
=======
>>>>>>> 81a65d3bf979f218ae8ce3684da07843bc4e2ea2
);

self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});

workbox.core.clientsClaim();

/**
 * The workboxSW.precacheAndRoute() method efficiently caches and responds to
 * requests for URLs in the manifest.
 * See https://goo.gl/S9QRab
 */
self.__precacheManifest = [].concat(self.__precacheManifest || []);
workbox.precaching.precacheAndRoute(self.__precacheManifest, {});

workbox.routing.registerNavigationRoute(workbox.precaching.getCacheKeyForURL("/index.html"), {
  
  blacklist: [/^\/_/,/\/[^\/]+\.[^\/]+$/],
});
