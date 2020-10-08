//?id=4507517029965059
function getUrlParams() {
    var obj = {}
    if (!location.search) return obj

    var query = location.search.substr(1)
    if (!query) return obj

    var arr1 = query.split("&") 
    arr1.forEach(r => {
        var arr2 = r.split("=")
        var key = arr2[0]
        var val = arr2[1]
        obj[key] = val
    })
    return obj;
}

function limit(str, length) {
    if (str.length < length)
        return str
    else return str.substr(0, length) + '...'
}
