<stats>
    <ul>
        <li>I dag: { data.stats.today }</li>
        <li>Denne uken: { data.stats.week }</li>
        <li>Denne mnd: { data.stats.month }</li>
    </ul>

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
