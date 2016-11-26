# setwd('C:/Ralph/Programming/777-data')
records = read.csv('records2.csv')
for (i in 1:nrow(records)) {
	if (records[i, "player1"] > records[i, "player2"]) {
		tmp = records[i, "player1"]
		records[i, "player1"] = records[i, "player2"]
		records[i, "player2"] = tmp
		tmp = records[i, "kills"]
		records[i, "kills"] = records[i, "deaths"]
		records[i, "deaths"] = tmp
	}
}

records2 = records
tmp = records2$player1
records2$player1 = records2$player2
records2$player2 = tmp
tmp = records2$kills
records2$kills = records2$deaths
records2$deaths = tmp
records = rbind(records, records2)
#default.win.rate = qbeta(0.4, mean(records$kills), mean(records$deaths))
default.win.rate = qbeta(0.4, mean(records$kills), mean(records$deaths))
#records$win.rate = (records$kills + 2) / (records$kills + records$deaths + 4)
records$win.rate = qbeta(0.4, records$kills + mean(records$kills), records$deaths + mean(records$deaths))
write.csv(records, file='win_records.csv')
hist(records$win.rate)

# leave-one-out test
# Bootstrap
errors = as.numeric(read.csv('errors.csv', header=FALSE))
errors.boot = sample(errors, length(errors) * 10000, replace=TRUE)
# IQR
errors.ci = tapply(errors.boot, rep(1:10000, length(errors)), function(x) { return(quantile(x, 0.75) - quantile(x, 0.25))})
quantile(errors, 0.75) - quantile(errors, 0.25)
quantile(errors.ci, 0.025)
quantile(errors.ci, 0.975)
# mean
errors.ci = tapply(errors.boot, rep(1:10000, length(errors)), mean)
mean(errors)
quantile(errors.ci, 0.025)
quantile(errors.ci, 0.975)
# variance
errors.ci = tapply(errors.boot, rep(1:10000, length(errors)), var)
var(errors)
quantile(errors.ci, 0.025)
quantile(errors.ci, 0.975)

# plot test data against true data
data = read.csv('inferred_test.csv')
plot(data[,6], data[,7], xlab='Actual skill', ylab='Predicted skill', xlim=c(0.1, 0.9), ylim=c(0.1, 0.9))
cor(data[,6], data[,7])
names(data)[4] = 'one.vs.two'
names(data)[5] = 'two.vs.three'
names(data)[6] = 'actual.skill'
names(data)[7] = 'predicted.skill'
model = lm(predicted.skill ~ actual.skill, data=data)
abline(model)
conf.data = seq(0, 1, 0.05)
conf.p = predict(model, newdata=data.frame(actual.skill=conf.data), interval=c('confidence'))
lines(conf.data, conf.p[,2], col='red', lty=2)
lines(conf.data, conf.p[,3], col='red', lty=2)

# actual ranking
data = read.csv('inferred_data.csv')
j = nrow(records) + 1
for (i in 1:nrow(data)) {
  if (data[i, 1] < data[i, 2]) {
    records[j,] = c(data[i, 1], data[i, 2], 0, 0, data[i, 3])
  } else {
    records[j,] = c(data[i, 2], data[i, 1], 0, 0, 1 - data[i, 3])
  }
  j = j + 1
}

records = records[order(records[,1]),]
unique.players = length(unique(c(records[,1], records[,2])))
rank.mean1 = aggregate(win.rate ~ player1, records, mean)
rank.mean2 = aggregate(win.rate ~ player2, records, mean)
rank.mean2$win.rate = 1 - rank.mean2$win.rate
names(rank.mean2)[1] = 'player1'
rank.mean = aggregate(win.rate ~ player1, rbind(rank.mean1, rank.mean2), mean)
rank.opponents = aggregate(player2 ~ player1, records, length)
rank.opponents2 = aggregate(player1 ~ player2, records, length)
names(rank.opponents2)[1] = 'player1'
names(rank.opponents2)[2] = 'player2'
j = nrow(rank.opponents) + 1
for (i in 1:nrow(rank.opponents2)) {
  if (!(rank.opponents2[i, 1] %in% rank.opponents[,1])) {
    rank.opponents[j,] = rank.opponents2[i,]
    j = j + 1
  }
  index = which(rank.opponents[,1] == rank.opponents2[i, 1])
  rank.opponents[index, 2] = rank.opponents[index, 2] + rank.opponents2[i, 2]
}

rank.opponents$coverage = rank.opponents[,2] / unique.players
for (i in 1:nrow(rank.mean)) {
  coverage = rank.opponents$coverage[which(rank.mean[i, 1] == rank.opponents[,1])]
  rank.mean[i, 2] = rank.mean[i, 2] * coverage + (1 - coverage) * default.win.rate
}
ranking = rank.mean[order(rank.mean[,2], decreasing=TRUE),]
ranking$rank = 1:unique.players

# output rank
write.csv(ranking, file='ranking.csv')
