<stats>
    <div>
    <p>I dag: { data.day.regular }</p>
    <p>Denne uke: { data.week.regular }</p>
    <p>Denne mnd: { data.month.regular }</p>
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
