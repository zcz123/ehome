var HashMap = function() {
  this.initialize();
}

HashMap.prototype = {
  hashkey_prefix: "<#HashMapHashkeyPerfix>",
  hashcode_field: "<#HashMapHashcodeField>",
  hashmap_instance_id: 0,

  initialize: function() {
    this.backing_hash = {};
    this.code = 0;
    this.hashmap_instance_id += 1;
    this.instance_id = this.hashmap_instance_id;
  },

  hashcodeField: function() {
    return this.hashcode_field + this.instance_id;
  },
  /*
   maps value to key returning previous assocciation
   */
  put: function(key, value) {
    var prev;

    if (key && value) {
      var hashCode;
      if (typeof(key) === "number" || typeof(key) === "string") {
        hashCode = key;
      } else {
        hashCode = key[this.hashcodeField()];
      }
      if (hashCode) {
        prev = this.backing_hash[hashCode];
      } else {
        this.code += 1;
        hashCode = this.hashkey_prefix + this.code;
        key[this.hashcodeField()] = hashCode;
      }
      this.backing_hash[hashCode] = [key, value];
    }
    return prev === undefined ? undefined : prev[1];
  },
  /*
   returns value associated with given key
   */
  get: function(key) {
    var value;
    if (key) {
      var hashCode;
      if (typeof(key) === "number" || typeof(key) === "string") {
        hashCode = key;
      } else {
        hashCode = key[this.hashcodeField()];
      }
      if (hashCode) {
        value = this.backing_hash[hashCode];
      }
    }
    return value === undefined ? undefined : value[1];
  },
  /*
   deletes association by given key.
   Returns true if the assocciation existed, false otherwise
   */
  del: function(key) {
    var success = false;
    if (key) {
      var hashCode;
      if (typeof(key) === "number" || typeof(key) === "string") {
        hashCode = key;
      } else {
        hashCode = key[this.hashcodeField()];
      }
      if (hashCode) {
        var prev = this.backing_hash[hashCode];
        this.backing_hash[hashCode] = undefined;
        if (prev !== undefined){
          key[this.hashcodeField()] = undefined; //let's clean the key object
          success = true;
        }
      }
    }
    return success;
  },
  /*
   iterate over key-value pairs passing them to provided callback
   the iteration process is interrupted when the callback returns false.
   the execution context of the callback is the value of the key-value pair
   @ returns the HashMap (so we can chain)                                                                  (
   */
  each: function(callback, args) {
    var key;
    for (key in this.backing_hash){
      if (callback.call(this.backing_hash[key][1], this.backing_hash[key][0], this.backing_hash[key][1]) === false)
        break;
    }
    return this;
  },
  toString: function() {
    return "HashMapJS"
  }

}