(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin', 'kotlin-wrappers-kotlin-react-dom-js-legacy', 'kotlinx-html-js', 'kotlin-wrappers-kotlin-react-js-legacy'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'), require('kotlin-wrappers-kotlin-react-dom-js-legacy'), require('kotlinx-html-js'), require('kotlin-wrappers-kotlin-react-js-legacy'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'natai'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'natai'.");
    }if (typeof this['kotlin-wrappers-kotlin-react-dom-js-legacy'] === 'undefined') {
      throw new Error("Error loading module 'natai'. Its dependency 'kotlin-wrappers-kotlin-react-dom-js-legacy' was not found. Please, check whether 'kotlin-wrappers-kotlin-react-dom-js-legacy' is loaded prior to 'natai'.");
    }if (typeof this['kotlinx-html-js'] === 'undefined') {
      throw new Error("Error loading module 'natai'. Its dependency 'kotlinx-html-js' was not found. Please, check whether 'kotlinx-html-js' is loaded prior to 'natai'.");
    }if (typeof this['kotlin-wrappers-kotlin-react-js-legacy'] === 'undefined') {
      throw new Error("Error loading module 'natai'. Its dependency 'kotlin-wrappers-kotlin-react-js-legacy' was not found. Please, check whether 'kotlin-wrappers-kotlin-react-js-legacy' is loaded prior to 'natai'.");
    }root.natai = factory(typeof natai === 'undefined' ? {} : natai, kotlin, this['kotlin-wrappers-kotlin-react-dom-js-legacy'], this['kotlinx-html-js'], this['kotlin-wrappers-kotlin-react-js-legacy']);
  }
}(this, function (_, Kotlin, $module$kotlin_wrappers_kotlin_react_dom_js_legacy, $module$kotlinx_html_js, $module$kotlin_wrappers_kotlin_react_js_legacy) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var getKClass = Kotlin.getKClass;
  var Unit = Kotlin.kotlin.Unit;
  var render = $module$kotlin_wrappers_kotlin_react_dom_js_legacy.react.dom.render_2955dm$;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var InputType = $module$kotlinx_html_js.kotlinx.html.InputType;
  var throwCCE = Kotlin.throwCCE;
  var set_onChangeFunction = $module$kotlinx_html_js.kotlinx.html.js.set_onChangeFunction_pszlq2$;
  var RComponent_init = $module$kotlin_wrappers_kotlin_react_js_legacy.react.RComponent_init_8bz2yq$;
  var RComponent = $module$kotlin_wrappers_kotlin_react_js_legacy.react.RComponent;
  var attributesMapOf = $module$kotlin_wrappers_kotlin_react_dom_js_legacy.$$importsForInline$$['kotlinx-html-js'].kotlinx.html.attributesMapOf_jyasbz$;
  var DIV_init = $module$kotlin_wrappers_kotlin_react_dom_js_legacy.$$importsForInline$$['kotlinx-html-js'].kotlinx.html.DIV;
  var RDOMBuilder = $module$kotlin_wrappers_kotlin_react_dom_js_legacy.react.dom.RDOMBuilder;
  var enumEncode = $module$kotlin_wrappers_kotlin_react_dom_js_legacy.$$importsForInline$$['kotlinx-html-js'].kotlinx.html.attributes.enumEncode_m4whry$;
  var attributesMapOf_0 = $module$kotlin_wrappers_kotlin_react_dom_js_legacy.$$importsForInline$$['kotlinx-html-js'].kotlinx.html.attributesMapOf_alerag$;
  var INPUT_init = $module$kotlin_wrappers_kotlin_react_dom_js_legacy.$$importsForInline$$['kotlinx-html-js'].kotlinx.html.INPUT;
  Welcome.prototype = Object.create(RComponent.prototype);
  Welcome.prototype.constructor = Welcome;
  function main$lambda$lambda$lambda$lambda($receiver) {
    $receiver.name = 'Kotlin/JS';
    return Unit;
  }
  function main$lambda$lambda$lambda($receiver) {
    $receiver.attrs_slhiwc$(main$lambda$lambda$lambda$lambda);
    return Unit;
  }
  function main$lambda$lambda($receiver) {
    $receiver.child_ssazr1$(getKClass(Welcome), main$lambda$lambda$lambda);
    return Unit;
  }
  function main$lambda(it) {
    render(document.getElementById('root'), void 0, main$lambda$lambda);
    return Unit;
  }
  function main() {
    window.onload = main$lambda;
  }
  function div$lambda(closure$classes) {
    return function (it) {
      return new DIV_init(attributesMapOf('class', closure$classes), it);
    };
  }
  function input$lambda(closure$type, closure$formEncType, closure$formMethod, closure$name, closure$classes) {
    return function (it) {
      return new INPUT_init(attributesMapOf_0(['type', closure$type != null ? enumEncode(closure$type) : null, 'formenctype', closure$formEncType != null ? enumEncode(closure$formEncType) : null, 'formmethod', closure$formMethod != null ? enumEncode(closure$formMethod) : null, 'name', closure$name, 'class', closure$classes]), it);
    };
  }
  function WelcomeState(name) {
    this.name = name;
  }
  WelcomeState.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'WelcomeState',
    interfaces: []
  };
  WelcomeState.prototype.component1 = function () {
    return this.name;
  };
  WelcomeState.prototype.copy_61zpoe$ = function (name) {
    return new WelcomeState(name === void 0 ? this.name : name);
  };
  WelcomeState.prototype.toString = function () {
    return 'WelcomeState(name=' + Kotlin.toString(this.name) + ')';
  };
  WelcomeState.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    return result;
  };
  WelcomeState.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && Kotlin.equals(this.name, other.name))));
  };
  function Welcome(props) {
    RComponent_init(props, this);
    this.state = new WelcomeState(props.name);
  }
  function Welcome$render$lambda$lambda$lambda(this$Welcome) {
    return function (event) {
      var tmp$, tmp$_0;
      tmp$_0 = new WelcomeState((Kotlin.isType(tmp$ = event.target, HTMLInputElement) ? tmp$ : throwCCE()).value);
      this$Welcome.setState(tmp$_0);
      return Unit;
    };
  }
  Welcome.prototype.render_ss14n$ = function ($receiver) {
    var $receiver_0 = RDOMBuilder.Companion.invoke_f6ihu2$(div$lambda(null));
    $receiver_0.unaryPlus_pdl1vz$('Hello, ' + this.state.name);
    $receiver.child_52psg1$($receiver_0.create());
    var $receiver_0_0 = RDOMBuilder.Companion.invoke_f6ihu2$(input$lambda(null, null, null, null, null));
    var $receiver_1 = $receiver_0_0.attrs;
    $receiver_1.type = InputType.text;
    $receiver_1.value = this.state.name;
    set_onChangeFunction($receiver_1, Welcome$render$lambda$lambda$lambda(this));
    $receiver.child_52psg1$($receiver_0_0.create());
  };
  Welcome.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Welcome',
    interfaces: [RComponent]
  };
  _.main = main;
  _.WelcomeState = WelcomeState;
  $$importsForInline$$['kotlin-wrappers-kotlin-react-dom-js-legacy'] = $module$kotlin_wrappers_kotlin_react_dom_js_legacy;
  _.Welcome = Welcome;
  main();
  Kotlin.defineModule('natai', _);
  return _;
}));

//# sourceMappingURL=natai.js.map
