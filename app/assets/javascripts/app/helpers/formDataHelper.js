const updateFormData = ($formData) => {
  if (!$formData) {
    return
  }
  const RICH_TEXT = '<!-- RICH_TEXT -->'
  $formData.forEach(($item, $key) => {
    let textSample = $item.substr(0, 50);

    // Correct an edge case where this is wrapped in a paragraph
    if (textSample.indexOf(`<p>${ RICH_TEXT }</p>`) !== -1) {
      $item = $item.replace(`<p>${ RICH_TEXT }</p>`, '')
      textSample = $item.substr(0, 30);
    }

    // Correct a further edge case where this is bold and wrapped in a paragraph
    if (textSample.indexOf(`<p><strong>${ RICH_TEXT }</strong></p>`) !== -1) {
      $item = $item.replace(`<p><strong>${ RICH_TEXT }</strong></p>`, '')
      textSample = $item.substr(0, 30);
    }

    if (/<\s*[a-z]+[^>]*>(.*?)<\s*\/\s*[a-z]+>/ig.test($item) && textSample.indexOf(RICH_TEXT) === -1) {
      $formData.set($key, RICH_TEXT + $item)
      const $formElement = document.getElementById($key)
      if ($formElement) {
        document.getElementById($key).value = RICH_TEXT + $item
      }
    }
  })
}

export {
  updateFormData
}
