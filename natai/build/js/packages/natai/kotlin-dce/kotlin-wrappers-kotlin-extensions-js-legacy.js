(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-extensions-js-legacy'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlin-wrappers-kotlin-extensions-js-legacy'.");
    }root['kotlin-wrappers-kotlin-extensions-js-legacy'] = factory(typeof this['kotlin-wrappers-kotlin-extensions-js-legacy'] === 'undefined' ? {} : this['kotlin-wrappers-kotlin-extensions-js-legacy'], kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var wrapFunction = Kotlin.wrapFunction;
  function clone(obj) {
    return Object.assign({}, obj);
  }
  var package$kotlinext = _.kotlinext || (_.kotlinext = {});
  var package$js = package$kotlinext.js || (package$kotlinext.js = {});
  package$js.clone_issdgt$ = clone;
  return _;
}));

//# sourceMappingURL=kotlin-wrappers-kotlin-extensions-js-legacy.js.map
