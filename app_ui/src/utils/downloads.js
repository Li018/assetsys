// 创建下载Excel方法
const downloadExcel = (res, name) => {
  const blob = new Blob([res.data])
  const a = document.createElement('a')
  const href = window.URL.createObjectURL(blob)
  a.href = href
  a.download = `${name}.xlsx`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  window.URL.revokeObjectURL(href)
}

export default downloadExcel
