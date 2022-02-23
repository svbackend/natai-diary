let config = {
  mode: 'production',
  resolve: {
    modules: [
      "node_modules"
    ]
  },
  plugins: [],
  module: {
    rules: []
  }
};

// entry
config.entry = {
    main: ["/home/vsaik/projects/diary/repository/natai/build/js/packages/natai/kotlin-dce/natai.js"]
};

config.output = {
    path: "/home/vsaik/projects/diary/repository/natai/build/distributions",
    filename: (chunkData) => {
        return chunkData.chunk.name === 'main'
            ? "natai.js"
            : "natai-[name].js";
    },
    library: "natai",
    libraryTarget: "umd",
    globalObject: "this"
};

// resolve modules
config.resolve.modules.unshift("/home/vsaik/projects/diary/repository/natai/build/js/packages/natai/kotlin-dce")

// source maps
config.module.rules.push({
        test: /\.js$/,
        use: ["source-map-loader"],
        enforce: "pre"
});
config.devtool = 'source-map';
config.ignoreWarnings = [/Failed to parse source map/]

// Report progress to console
// noinspection JSUnnecessarySemicolon
;(function(config) {
    const webpack = require('webpack');
    const handler = (percentage, message, ...args) => {
        const p = percentage * 100;
        let msg = `${Math.trunc(p / 10)}${Math.trunc(p % 10)}% ${message} ${args.join(' ')}`;
        msg = msg.replace(new RegExp("/home/vsaik/projects/diary/repository/natai/build/js", 'g'), '');;
        console.log(msg);
    };

    config.plugins.push(new webpack.ProgressPlugin(handler))
})(config);

// css settings
;(function(config) {
    ;(function(config) {
       const use = [
           {
               loader: 'css-loader',
               options: {},
           }
       ]
       use.unshift({
           loader: 'style-loader',
           options: {}
       })
       
       config.module.rules.push({
           test: /\.css$/,
           use: use,
           
           
       })

   })(config);
            
})(config);

// noinspection JSUnnecessarySemicolon
;(function(config) {
    const tcErrorPlugin = require('kotlin-test-js-runner/tc-log-error-webpack');
    config.plugins.push(new tcErrorPlugin())
    config.stats = config.stats || {}
    Object.assign(config.stats, config.stats, {
        warnings: false,
        errors: false
    })
})(config);
// save evaluated config file
;(function(config) {
    const util = require('util');
    const fs = require('fs');
    const evaluatedConfig = util.inspect(config, {showHidden: false, depth: null, compact: false});
    fs.writeFile("/home/vsaik/projects/diary/repository/natai/build/reports/webpack/natai/webpack.config.evaluated.js", evaluatedConfig, function (err) {});
})(config);

module.exports = config
