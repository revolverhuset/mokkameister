"use strict";

moment.locale('nn');
riot.mount('status', { url: '/status', interval: 10000 });
riot.mount('stats', { url: '/stats', interval: 10000 });
