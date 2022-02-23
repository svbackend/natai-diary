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
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var getCallableRef = Kotlin.getCallableRef;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var wrapFunction = Kotlin.wrapFunction;
  function requireAll(context) {
    var $receiver = context.keys();
    var action = getCallableRef('invoke', function ($receiver, p1) {
      return invoke_0($receiver, p1);
    }.bind(null, context));
    var tmp$;
    for (tmp$ = 0; tmp$ !== $receiver.length; ++tmp$) {
      var element = $receiver[tmp$];
      action(element);
    }
  }
  function invoke($receiver) {
    return $receiver();
  }
  function invoke_0($receiver, arg) {
    return $receiver(arg);
  }
  function invoke_1($receiver, arg1, arg2) {
    return $receiver(arg1, arg2);
  }
  function invoke_2($receiver, arg1, arg2, arg3) {
    return $receiver(arg1, arg2, arg3);
  }
  var jsObject = defineInlineFunction('kotlin-wrappers-kotlin-extensions-js-legacy.kotlinext.js.jsObject_30y1fr$', function () {
    return {};
  });
  var jsObject_0 = defineInlineFunction('kotlin-wrappers-kotlin-extensions-js-legacy.kotlinext.js.jsObject_dajwzo$', function (builder) {
    var $receiver = {};
    builder($receiver);
    return $receiver;
  });
  var js = defineInlineFunction('kotlin-wrappers-kotlin-extensions-js-legacy.kotlinext.js.js_5ij4lk$', function (builder) {
    var $receiver = {};
    builder($receiver);
    return $receiver;
  });
  function clone(obj) {
    return Object.assign({}, obj);
  }
  var assign = defineInlineFunction('kotlin-wrappers-kotlin-extensions-js-legacy.kotlinext.js.assign_bjvcay$', wrapFunction(function () {
    var clone = _.kotlinext.js.clone_issdgt$;
    return function (obj, builder) {
      var $receiver = clone(obj);
      builder($receiver);
      return $receiver;
    };
  }));
  function toPlainObjectStripNull(obj) {
    var $receiver = {};
    var tmp$, tmp$_0;
    tmp$ = Object.keys(obj);
    for (tmp$_0 = 0; tmp$_0 !== tmp$.length; ++tmp$_0) {
      var key = tmp$[tmp$_0];
      var value = obj[key];
      if (value != null)
        $receiver[key] = value;
    }
    return $receiver;
  }
  function asJsObject($receiver) {
    return $receiver;
  }
  function getOwnPropertyNames($receiver) {
    return Object.getOwnPropertyNames($receiver);
  }
  var get = defineInlineFunction('kotlin-wrappers-kotlin-extensions-js-legacy.kotlinext.js.get_3llx5$', function ($receiver, key) {
    return $receiver[key];
  });
  var set = defineInlineFunction('kotlin-wrappers-kotlin-extensions-js-legacy.kotlinext.js.set_ne2z2k$', function ($receiver, key, value) {
    $receiver[key] = value;
  });
  function Record(block) {
    var $receiver = {};
    block($receiver);
    return $receiver;
  }
  function invoke_3($receiver, strings, values) {
    var tmp$;
    return (tmp$ = $receiver).call.apply(tmp$, [null, strings].concat(values));
  }
  function invoke_4($receiver, string, values) {
    return invoke_3($receiver, [string], values.slice());
  }
  function invoke_5($receiver, values) {
    return invoke_3($receiver, [], values.slice());
  }
  var package$kotlinext = _.kotlinext || (_.kotlinext = {});
  var package$js = package$kotlinext.js || (package$kotlinext.js = {});
  package$js.requireAll_spd3bs$ = requireAll;
  package$js.invoke_o1mxae$ = invoke;
  package$js.invoke_nbfla$ = invoke_0;
  package$js.invoke_xbkh9p$ = invoke_1;
  package$js.invoke_ahlu6z$ = invoke_2;
  package$js.jsObject_30y1fr$ = jsObject;
  $$importsForInline$$['kotlin-wrappers-kotlin-extensions-js-legacy'] = _;
  package$js.jsObject_dajwzo$ = jsObject_0;
  package$js.js_5ij4lk$ = js;
  package$js.clone_issdgt$ = clone;
  package$js.assign_bjvcay$ = assign;
  package$js.toPlainObjectStripNull_za3rmp$ = toPlainObjectStripNull;
  package$js.asJsObject_s8jyvk$ = asJsObject;
  package$js.getOwnPropertyNames_s8jyvk$ = getOwnPropertyNames;
  package$js.get_3llx5$ = get;
  package$js.set_ne2z2k$ = set;
  package$js.Record_5n88ol$ = Record;
  package$js.invoke_z5wujd$ = invoke_3;
  package$js.invoke_dgimx$ = invoke_4;
  package$js.invoke_9p99ed$ = invoke_5;
  Kotlin.defineModule('kotlin-wrappers-kotlin-extensions-js-legacy', _);
  return _;
}));
