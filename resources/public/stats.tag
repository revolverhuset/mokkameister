<stats>
    <div>
    <p>I dag: { data.regular.today }</p>
    <p>I gaar: { data.regular.yesterday }</p>
    <p>Denne uke: { data.regular.thisweek }</p>
    <p>Sist uke: { data.regular.lastweek }</p>
    <p>Denne mnd: { data.regular.thismonth }</p>
    <p>Sist mnd: { data.regular.lastmonth }</p>
    </div>

    this.data = {}

    load() {
        var self = this
        $.ajax({
            url: opts.url,
            dataType: 'json',
            cache: false,
            success: function(d) {
                self.data = d
                self.update()
            }})
    }

    this.load()
    setInterval(this.load, opts.interval)
</stats>
