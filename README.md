[![GitHub issues](https://img.shields.io/github/issues/IoTers/Click-Through-Rate-Prediction.svg)](https://github.com/IoTers/Click-Through-Rate-Prediction/issues) [![Wercker](https://img.shields.io/wercker/ci/wercker/docs.svg?maxAge=2592000)]() [![GitHub forks](https://img.shields.io/github/forks/IoTers/Click-Through-Rate-Prediction.svg)](https://github.com/IoTers/Click-Through-Rate-Prediction/network)

# Click-Through-Rate-Prediction
In online advertising, click-through rate (CTR) is a very important metric for evaluating ad performance. As a result, click prediction systems are essential and widely used for sponsored search and real-time bidding.

# Feature Selection
- label : 0/1 for non-click/click
- id
- hour =>  0600-13:1, 1400-21:2, 2200-05:3
- banner_pos
- site_id
- site_category
- app_id
- app_category
- device_id
- device_ip
- device_model
- device_type
- C1, C14-C21 -- anonymized categorical variables

# Dependencies
- Spark 1.6.1
- Scala 2.10.4
- SBT 0.13.8

# 說明
總共有三種Model: SVM, Logistic Regression, Random Forest。

#### Step 1 
使用 spark 實作 Recursive Feature Elimination, 找出適當的屬性

#### Step 2 建模
Hyperparameter tunning:  return model which has the best AUC Area 

#### Step 3 投票
每個模型的建立過程會先做以投票方式產生Label。

#### Step 4 計算投票的AUC Area