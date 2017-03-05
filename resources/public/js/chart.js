/*
Copyright 2017 Revolverhuset.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
(function (name, root, factory) {
    function isObject(x) { return typeof x === "object"; }
    if (isObject(root.module) && isObject(root.module.exports)) {
        root.module.exports = factory();
    } else if (isObject(root.exports)) {
        root.exports[name] = factory();
    } else if (isObject(root.define) && root.define.amd) {
        root.define(name, [], factory);
    } else if (isObject(root.modulejs)) {
        root.modulejs.define(name, factory);
    } else if (isObject(root.YUI)) {
        root.YUI.add(name, function (Y) { Y[name] = factory(); });
    } else {
        root[name] = factory();
    }
}("chart", this, function () {
    // Copied from https://github.com/jstrace/chart
    function matrix(x, y) {
        var arr = new Array(y);

        for (var i = 0; i < y; i++) {
            arr[i] = new Array(x);
        }

        return arr;
    }

    function chart(data, opts) {
        opts = opts || {};

        // options
        var w = opts.width || 130;
        var h = opts.height || 30;
        var pc = opts.pointChar || '█';
        var nc = opts.negativePointChar || '░';
        var ac = opts.axisChar || '.';

        // padding
        var pad = typeof opts.padding === 'number' ? opts.padding : 3;
        w -= pad * 2;
        h -= pad * 2;

        // setup
        var out = matrix(w,h);
        var m = max(data) || 0;
        var label = Math.abs(m).toString();
        var labelw = label.length;
        var labelp = 1;

        // chart sizes void of padding etc
        var ch = h;
        var cw = w - labelw - labelp;

        // fill
        for (var y = 0; y < h; y++) {
            for (var x = 0; x < w; x++) {
                out[y][x] = ' ';
            }
        }

        // y-axis labels
        for (var i = 0; i < labelw; i++) {
            out[0][i] = label[i];
        }

        out[h - 1][labelw - labelp] = '0';

        // y-axis
        for (var y = 0; y < h; y++) {
            out[y][labelw + labelp] = ac;
        }

        // x-axis
        var x = labelw + labelp;
        while (x < w) {
            out[h - 1][x++] = ac;
            out[h - 1][x++] = ' ';
        }

        // strip excess from head
        // so that data may "roll"
        var space = Math.floor(cw / 2) - 1;
        var excess = Math.max(0, data.length - space);
        if (excess) data = data.slice(excess);

        // plot data
        var x = labelw + labelp + 2;
        for (var i = 0; i < data.length; i++) {
            var d = data[i];
            var p = d / m;
            var y = Math.round((h - 2) * p);
            var c = y < 0 ? nc : pc;
            if (y < 0) y = -y;

            while (y--) {
                out[Math.abs(y - h) - 2][x] = c;
            }

            x += 2;
        }

        // Return string
        var str = string(out, h);
        return pad ? padding(str, pad) : str;
    }

    /**
     * Apply padding.
     */

    function padding(str, n) {
        var linew = str.split('\n')[0].length;
        var line = Array(linew).join(' ') + '\n';

        // y
        str = Array(n).join(line) + str;
        str = str + Array(n).join(line);

        // x
        str = str.replace(/^/gm, Array(n).join(' '));
        return str;
    }

    /**
     * Convert matrix to a string.
     */

    function string(out) {
        var buf = [];

        for (var i = 0; i < out.length; i++) {
            buf.push(out[i].join(''));
        }

        return buf.join('\n');
    }

    /**
     * Return max in array.
     */

    function max(data) {
        var n = Math.abs(data[0]);

        for (var i = 1; i < data.length; i++) {
            n = Math.abs(data[i]) > n ? Math.abs(data[i]) : n;
        }

        return n;
    }

    return chart;
}));
