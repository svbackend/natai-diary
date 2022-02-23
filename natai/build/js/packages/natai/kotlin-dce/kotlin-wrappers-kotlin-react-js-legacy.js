(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin', 'react', 'kotlin-wrappers-kotlin-extensions-js-legacy'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'), require('react'), require('kotlin-wrappers-kotlin-extensions-js-legacy'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-react-js-legacy'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlin-wrappers-kotlin-react-js-legacy'.");
    }if (typeof react === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-react-js-legacy'. Its dependency 'react' was not found. Please, check whether 'react' is loaded prior to 'kotlin-wrappers-kotlin-react-js-legacy'.");
    }if (typeof this['kotlin-wrappers-kotlin-extensions-js-legacy'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-wrappers-kotlin-react-js-legacy'. Its dependency 'kotlin-wrappers-kotlin-extensions-js-legacy' was not found. Please, check whether 'kotlin-wrappers-kotlin-extensions-js-legacy' is loaded prior to 'kotlin-wrappers-kotlin-react-js-legacy'.");
    }root['kotlin-wrappers-kotlin-react-js-legacy'] = factory(typeof this['kotlin-wrappers-kotlin-react-js-legacy'] === 'undefined' ? {} : this['kotlin-wrappers-kotlin-react-js-legacy'], kotlin, react, this['kotlin-wrappers-kotlin-extensions-js-legacy']);
  }
}(this, function (_, Kotlin, $module$react, $module$kotlin_wrappers_kotlin_extensions_js_legacy) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var Unit = Kotlin.kotlin.Unit;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var createElement = $module$react.createElement;
  var clone = $module$kotlin_wrappers_kotlin_extensions_js_legacy.kotlinext.js.clone_issdgt$;
  var emptyList = Kotlin.kotlin.collections.emptyList_287e2$;
  var listOf = Kotlin.kotlin.collections.listOf_mh5how$;
  var Children = $module$react.Children;
  var addAll = Kotlin.kotlin.collections.addAll_ye1y7v$;
  var throwCCE = Kotlin.throwCCE;
  var cloneElement = $module$react.cloneElement;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var wrapFunction = Kotlin.wrapFunction;
  var first = Kotlin.kotlin.collections.first_2p1efm$;
  var copyToArray = Kotlin.kotlin.collections.copyToArray;
  var collectionSizeOrDefault = Kotlin.kotlin.collections.collectionSizeOrDefault_ba2ldo$;
  var ArrayList_init = Kotlin.kotlin.collections.ArrayList_init_ww73n8$;
  var checkIndexOverflow = Kotlin.kotlin.collections.checkIndexOverflow_za3lpa$;
  var ArrayList_init_0 = Kotlin.kotlin.collections.ArrayList_init_287e2$;
  var IllegalStateException_init = Kotlin.kotlin.IllegalStateException_init_pdl1vj$;
  var get_js = Kotlin.kotlin.js.get_js_1yb8b7$;
  var Component = $module$react.Component;
  var Any = Object;
  var to = Kotlin.kotlin.to_ujzrz7$;
  RElementBuilderImpl.prototype = Object.create(RBuilderImpl.prototype);
  RElementBuilderImpl.prototype.constructor = RElementBuilderImpl;
  RComponent.prototype = Object.create(Component.prototype);
  RComponent.prototype.constructor = RComponent;
  function RBuilder() {
  }
  RBuilder.prototype.child_52psg1$ = function (element) {
    this.childList.add_11rb$(element);
    return element;
  };
  RBuilder.prototype.unaryPlus_84gpoi$ = function ($receiver) {
    this.childList.add_11rb$($receiver);
  };
  RBuilder.prototype.unaryPlus_pdl1vz$ = function ($receiver) {
    this.childList.add_11rb$($receiver);
  };
  RBuilder.prototype.child_k3oess$ = function (type, props, children) {
    return this.child_52psg1$(createElement.apply(null, [type, props].concat(copyToArray(children))));
  };
  RBuilder.prototype.child_4dvv5y$ = function (type, props, handler) {
    var $receiver = RElementBuilder_0(props);
    handler($receiver);
    var children = $receiver.childList;
    return this.child_k3oess$(type, props, children);
  };
  RBuilder.prototype.invoke_eb8iu4$ = function ($receiver, handler) {
    return this.child_4dvv5y$($receiver, {}, handler);
  };
  RBuilder.prototype.invoke_csqs6z$ = function ($receiver, value, handler) {
    var $receiver_0 = {};
    $receiver_0.value = value;
    return this.child_4dvv5y$($receiver, $receiver_0, handler);
  };
  function RBuilder$invoke$lambda$lambda$lambda(closure$handler, closure$value) {
    return function ($receiver) {
      closure$handler($receiver, closure$value);
      return Unit;
    };
  }
  function RBuilder$invoke$lambda$lambda(closure$handler) {
    return function (value) {
      return buildElements_0(RBuilder$invoke$lambda$lambda$lambda(closure$handler, value));
    };
  }
  function RBuilder$invoke$lambda($receiver) {
    return Unit;
  }
  RBuilder.prototype.invoke_ory6b3$ = function ($receiver, handler) {
    var $receiver_0 = {};
    $receiver_0.children = RBuilder$invoke$lambda$lambda(handler);
    return this.child_4dvv5y$($receiver, $receiver_0, RBuilder$invoke$lambda);
  };
  RBuilder.prototype.node_rwypko$$default = function ($receiver, props, children) {
    return this.child_k3oess$($receiver, clone(props), children);
  };
  RBuilder.prototype.node_rwypko$ = function ($receiver, props, children, callback$default) {
    if (children === void 0)
      children = emptyList();
    return callback$default ? callback$default($receiver, props, children) : this.node_rwypko$$default($receiver, props, children);
  };
  RBuilder.prototype.child_ssazr1$ = function (klazz, handler) {
    return this.invoke_eb8iu4$(get_rClass(klazz), handler);
  };
  function RBuilder$childFunction$lambda(closure$children) {
    return function (value) {
      var rBuilder = RBuilder_0();
      closure$children(rBuilder, value);
      return first(rBuilder.childList);
    };
  }
  RBuilder.prototype.childFunction_2656uf$ = function (klazz, handler, children) {
    var tmp$ = get_rClass(klazz);
    var $receiver = RElementBuilder_0({});
    handler($receiver);
    return this.child_k3oess$(tmp$, $receiver.attrs, listOf(RBuilder$childFunction$lambda(children)));
  };
  RBuilder.prototype.node_3ecl1l$$default = function (klazz, props, children) {
    return this.node_rwypko$(get_rClass(klazz), props, children);
  };
  RBuilder.prototype.node_3ecl1l$ = function (klazz, props, children, callback$default) {
    if (children === void 0)
      children = emptyList();
    return callback$default ? callback$default(klazz, props, children) : this.node_3ecl1l$$default(klazz, props, children);
  };
  RBuilder.prototype.children_yllgzm$ = function ($receiver) {
    addAll(this.childList, Children.toArray(get_children($receiver)));
  };
  RBuilder.prototype.children_48djri$ = function ($receiver, value) {
    var tmp$;
    this.childList.add_11rb$((typeof (tmp$ = get_children($receiver)) === 'function' ? tmp$ : throwCCE())(value));
  };
  RBuilder.prototype.renderEach_ezdo97$ = function ($receiver, fn) {
    var tmp$ = this.childList;
    var destination = ArrayList_init(collectionSizeOrDefault($receiver, 10));
    var tmp$_0;
    tmp$_0 = $receiver.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      var tmp$_1 = destination.add_11rb$;
      var rBuilder = RBuilder_0();
      fn(rBuilder, item);
      tmp$_1.call(destination, first(rBuilder.childList));
    }
    tmp$.add_11rb$(copyToArray(destination));
  };
  RBuilder.prototype.renderEachIndexed_cfwqen$ = function ($receiver, fn) {
    var tmp$ = this.childList;
    var destination = ArrayList_init(collectionSizeOrDefault($receiver, 10));
    var tmp$_0, tmp$_0_0;
    var index = 0;
    tmp$_0 = $receiver.iterator();
    while (tmp$_0.hasNext()) {
      var item = tmp$_0.next();
      var tmp$_1 = destination.add_11rb$;
      var index_0 = checkIndexOverflow((tmp$_0_0 = index, index = tmp$_0_0 + 1 | 0, tmp$_0_0));
      var rBuilder = RBuilder_0();
      fn(rBuilder, index_0, item);
      tmp$_1.call(destination, first(rBuilder.childList));
    }
    tmp$.add_11rb$(copyToArray(destination));
  };
  RBuilder.prototype.withKey_s5hl0b$ = function ($receiver, newKey) {
    var index = this.childList.indexOf_11rb$($receiver);
    if (index >= 0) {
      this.childList.removeAt_za3lpa$(index);
      var $receiver_0 = {};
      set_key($receiver_0, newKey);
      var elementWithKey = cloneElement($receiver, $receiver_0);
      this.childList.add_wxm5ur$(index, elementWithKey);
    }};
  RBuilder.prototype.withKey_pspxar$ = function ($receiver, newKey) {
    this.withKey_s5hl0b$($receiver, newKey.toString());
  };
  RBuilder.$metadata$ = {kind: Kind_INTERFACE, simpleName: 'RBuilder', interfaces: []};
  function RBuilder_0() {
    return new RBuilderImpl();
  }
  function RBuilderImpl() {
    this.childList_z394dm$_0 = ArrayList_init_0();
  }
  Object.defineProperty(RBuilderImpl.prototype, 'childList', {configurable: true, get: function () {
    return this.childList_z394dm$_0;
  }});
  RBuilderImpl.$metadata$ = {kind: Kind_CLASS, simpleName: 'RBuilderImpl', interfaces: [RBuilder]};
  function buildElements(builder, handler) {
    var tmp$;
    handler(builder);
    var nodes = builder.childList;
    switch (nodes.size) {
      case 0:
        tmp$ = null;
        break;
      case 1:
        tmp$ = first(nodes);
        break;
      default:var tmp$_0 = $module$react.Fragment;
        var $receiver = {};
        tmp$ = createElement.apply(null, [tmp$_0, $receiver].concat(copyToArray(nodes)));
        break;
    }
    return tmp$;
  }
  function buildElements_0(handler) {
    return buildElements(RBuilder_0(), handler);
  }
  function RElementBuilder() {
  }
  RElementBuilder.prototype.attrs_slhiwc$ = function (handler) {
    handler(this.attrs);
  };
  Object.defineProperty(RElementBuilder.prototype, 'key', {configurable: true, get: function () {
    throw IllegalStateException_init(''.toString());
  }, set: function (value) {
    set_key(this.attrs, value);
  }});
  Object.defineProperty(RElementBuilder.prototype, 'ref', {configurable: true, get: function () {
    throw IllegalStateException_init(''.toString());
  }, set: function (value) {
    set_ref(this.attrs, value);
  }});
  RElementBuilder.prototype.ref_5ij4lk$ = function (handler) {
    ref(this.attrs, handler);
  };
  RElementBuilder.$metadata$ = {kind: Kind_INTERFACE, simpleName: 'RElementBuilder', interfaces: [RBuilder]};
  function RElementBuilder_0(attrs) {
    return new RElementBuilderImpl(attrs);
  }
  function RElementBuilderImpl(attrs) {
    RBuilderImpl.call(this);
    this.attrs_rox8qs$_0 = attrs;
  }
  Object.defineProperty(RElementBuilderImpl.prototype, 'attrs', {get: function () {
    return this.attrs_rox8qs$_0;
  }});
  RElementBuilderImpl.$metadata$ = {kind: Kind_CLASS, simpleName: 'RElementBuilderImpl', interfaces: [RBuilderImpl, RElementBuilder]};
  function get_rClass($receiver) {
    return get_js($receiver);
  }
  function get_children($receiver) {
    return $receiver.children;
  }
  function set_key($receiver, value) {
    $receiver.key = value;
  }
  function set_ref($receiver, value) {
    $receiver.ref = value;
  }
  function ref($receiver, ref) {
    $receiver.ref = ref;
  }
  function RComponent() {
  }
  RComponent.prototype.init_bc6fkx$ = function ($receiver) {
  };
  RComponent.prototype.init_65a95q$ = function ($receiver, props) {
  };
  RComponent.prototype.children_ss14n$ = function ($receiver) {
    $receiver.children_yllgzm$(this.props);
  };
  RComponent.prototype.children_tgvp6h$ = function ($receiver, value) {
    $receiver.children_48djri$(this.props, value);
  };
  function RComponent$render$lambda(this$RComponent) {
    return function ($receiver) {
      this$RComponent.render_ss14n$($receiver);
      return Unit;
    };
  }
  RComponent.prototype.render = function () {
    return buildElements_0(RComponent$render$lambda(this));
  };
  RComponent.$metadata$ = {kind: Kind_CLASS, simpleName: 'RComponent', interfaces: []};
  function RComponent_init_0(props, $this) {
    $this = $this || Object.create(RComponent.prototype);
    Component.call($this, props);
    RComponent.call($this);
    var $receiver = {};
    $this.init_65a95q$($receiver, props);
    $this.state = $receiver;
    return $this;
  }
  var package$react = _.react || (_.react = {});
  $$importsForInline$$['kotlin-wrappers-kotlin-extensions-js-legacy'] = $module$kotlin_wrappers_kotlin_extensions_js_legacy;
  package$react.RBuilder = RBuilder;
  package$react.createBuilder = RBuilder_0;
  package$react.RBuilderImpl = RBuilderImpl;
  package$react.buildElements_wi9ndb$ = buildElements;
  package$react.buildElements_zepujl$ = buildElements_0;
  package$react.RElementBuilder = RElementBuilder;
  package$react.RElementBuilder_8jcap1$ = RElementBuilder_0;
  package$react.RElementBuilderImpl = RElementBuilderImpl;
  $$importsForInline$$.react = $module$react;
  package$react.get_rClass_inwa2g$ = get_rClass;
  package$react.get_children_yllgzm$ = get_children;
  package$react.set_key_38rnt0$ = set_key;
  package$react.set_ref_jjyqia$ = set_ref;
  package$react.ref_dpkau5$ = ref;
  package$react.RComponent_init_8bz2yq$ = RComponent_init_0;
  package$react.RComponent = RComponent;
  RBuilderImpl.prototype.child_52psg1$ = RBuilder.prototype.child_52psg1$;
  RBuilderImpl.prototype.child_k3oess$ = RBuilder.prototype.child_k3oess$;
  RBuilderImpl.prototype.child_4dvv5y$ = RBuilder.prototype.child_4dvv5y$;
  RBuilderImpl.prototype.child_ssazr1$ = RBuilder.prototype.child_ssazr1$;
  RBuilderImpl.prototype.unaryPlus_84gpoi$ = RBuilder.prototype.unaryPlus_84gpoi$;
  RBuilderImpl.prototype.unaryPlus_pdl1vz$ = RBuilder.prototype.unaryPlus_pdl1vz$;
  RBuilderImpl.prototype.invoke_eb8iu4$ = RBuilder.prototype.invoke_eb8iu4$;
  RBuilderImpl.prototype.invoke_csqs6z$ = RBuilder.prototype.invoke_csqs6z$;
  RBuilderImpl.prototype.invoke_ory6b3$ = RBuilder.prototype.invoke_ory6b3$;
  RBuilderImpl.prototype.node_rwypko$$default = RBuilder.prototype.node_rwypko$$default;
  RBuilderImpl.prototype.node_3ecl1l$$default = RBuilder.prototype.node_3ecl1l$$default;
  RBuilderImpl.prototype.childFunction_2656uf$ = RBuilder.prototype.childFunction_2656uf$;
  RBuilderImpl.prototype.children_yllgzm$ = RBuilder.prototype.children_yllgzm$;
  RBuilderImpl.prototype.children_48djri$ = RBuilder.prototype.children_48djri$;
  RBuilderImpl.prototype.renderEach_ezdo97$ = RBuilder.prototype.renderEach_ezdo97$;
  RBuilderImpl.prototype.renderEachIndexed_cfwqen$ = RBuilder.prototype.renderEachIndexed_cfwqen$;
  RBuilderImpl.prototype.withKey_s5hl0b$ = RBuilder.prototype.withKey_s5hl0b$;
  RBuilderImpl.prototype.withKey_pspxar$ = RBuilder.prototype.withKey_pspxar$;
  RBuilderImpl.prototype.node_rwypko$ = RBuilder.prototype.node_rwypko$;
  RBuilderImpl.prototype.node_3ecl1l$ = RBuilder.prototype.node_3ecl1l$;
  RElementBuilder.prototype.child_52psg1$ = RBuilder.prototype.child_52psg1$;
  RElementBuilder.prototype.child_k3oess$ = RBuilder.prototype.child_k3oess$;
  RElementBuilder.prototype.child_4dvv5y$ = RBuilder.prototype.child_4dvv5y$;
  RElementBuilder.prototype.child_ssazr1$ = RBuilder.prototype.child_ssazr1$;
  RElementBuilder.prototype.unaryPlus_84gpoi$ = RBuilder.prototype.unaryPlus_84gpoi$;
  RElementBuilder.prototype.unaryPlus_pdl1vz$ = RBuilder.prototype.unaryPlus_pdl1vz$;
  RElementBuilder.prototype.invoke_eb8iu4$ = RBuilder.prototype.invoke_eb8iu4$;
  RElementBuilder.prototype.invoke_csqs6z$ = RBuilder.prototype.invoke_csqs6z$;
  RElementBuilder.prototype.invoke_ory6b3$ = RBuilder.prototype.invoke_ory6b3$;
  RElementBuilder.prototype.node_rwypko$$default = RBuilder.prototype.node_rwypko$$default;
  RElementBuilder.prototype.node_3ecl1l$$default = RBuilder.prototype.node_3ecl1l$$default;
  RElementBuilder.prototype.childFunction_2656uf$ = RBuilder.prototype.childFunction_2656uf$;
  RElementBuilder.prototype.children_yllgzm$ = RBuilder.prototype.children_yllgzm$;
  RElementBuilder.prototype.children_48djri$ = RBuilder.prototype.children_48djri$;
  RElementBuilder.prototype.renderEach_ezdo97$ = RBuilder.prototype.renderEach_ezdo97$;
  RElementBuilder.prototype.renderEachIndexed_cfwqen$ = RBuilder.prototype.renderEachIndexed_cfwqen$;
  RElementBuilder.prototype.withKey_s5hl0b$ = RBuilder.prototype.withKey_s5hl0b$;
  RElementBuilder.prototype.withKey_pspxar$ = RBuilder.prototype.withKey_pspxar$;
  RElementBuilder.prototype.node_rwypko$ = RBuilder.prototype.node_rwypko$;
  RElementBuilder.prototype.node_3ecl1l$ = RBuilder.prototype.node_3ecl1l$;
  RElementBuilderImpl.prototype.attrs_slhiwc$ = RElementBuilder.prototype.attrs_slhiwc$;
  Object.defineProperty(RElementBuilderImpl.prototype, 'key', Object.getOwnPropertyDescriptor(RElementBuilder.prototype, 'key'));
  Object.defineProperty(RElementBuilderImpl.prototype, 'ref', Object.getOwnPropertyDescriptor(RElementBuilder.prototype, 'ref'));
  RElementBuilderImpl.prototype.ref_5ij4lk$ = RElementBuilder.prototype.ref_5ij4lk$;
  RElementBuilderImpl.prototype.child_52psg1$ = RElementBuilder.prototype.child_52psg1$;
  RElementBuilderImpl.prototype.child_k3oess$ = RElementBuilder.prototype.child_k3oess$;
  RElementBuilderImpl.prototype.child_4dvv5y$ = RElementBuilder.prototype.child_4dvv5y$;
  RElementBuilderImpl.prototype.child_ssazr1$ = RElementBuilder.prototype.child_ssazr1$;
  RElementBuilderImpl.prototype.unaryPlus_84gpoi$ = RElementBuilder.prototype.unaryPlus_84gpoi$;
  RElementBuilderImpl.prototype.unaryPlus_pdl1vz$ = RElementBuilder.prototype.unaryPlus_pdl1vz$;
  RElementBuilderImpl.prototype.invoke_eb8iu4$ = RElementBuilder.prototype.invoke_eb8iu4$;
  RElementBuilderImpl.prototype.invoke_csqs6z$ = RElementBuilder.prototype.invoke_csqs6z$;
  RElementBuilderImpl.prototype.invoke_ory6b3$ = RElementBuilder.prototype.invoke_ory6b3$;
  RElementBuilderImpl.prototype.node_rwypko$$default = RElementBuilder.prototype.node_rwypko$$default;
  RElementBuilderImpl.prototype.node_3ecl1l$$default = RElementBuilder.prototype.node_3ecl1l$$default;
  RElementBuilderImpl.prototype.childFunction_2656uf$ = RElementBuilder.prototype.childFunction_2656uf$;
  RElementBuilderImpl.prototype.children_yllgzm$ = RElementBuilder.prototype.children_yllgzm$;
  RElementBuilderImpl.prototype.children_48djri$ = RElementBuilder.prototype.children_48djri$;
  RElementBuilderImpl.prototype.renderEach_ezdo97$ = RElementBuilder.prototype.renderEach_ezdo97$;
  RElementBuilderImpl.prototype.renderEachIndexed_cfwqen$ = RElementBuilder.prototype.renderEachIndexed_cfwqen$;
  RElementBuilderImpl.prototype.withKey_s5hl0b$ = RElementBuilder.prototype.withKey_s5hl0b$;
  RElementBuilderImpl.prototype.withKey_pspxar$ = RElementBuilder.prototype.withKey_pspxar$;
  RElementBuilderImpl.prototype.node_rwypko$ = RElementBuilder.prototype.node_rwypko$;
  RElementBuilderImpl.prototype.node_3ecl1l$ = RElementBuilder.prototype.node_3ecl1l$;
  return _;
}));

//# sourceMappingURL=kotlin-wrappers-kotlin-react-js-legacy.js.map
