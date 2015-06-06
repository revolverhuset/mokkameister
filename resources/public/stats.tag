<stats>
    <ul>
        <li>I dag: { data.day.regular }</li>
        <li>Denne uken: { data.week.regular }</li>
        <li>Denne mnd: { data.month.regular }</li>
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
