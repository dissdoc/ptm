class CreateRecommendAts < ActiveRecord::Migration
  def change
    create_table :recommend_ats do |t|
      t.datetime :from_at
      t.datetime :to_at
      t.integer :user_id
      t.integer :photo_id

      t.timestamps
    end
  end
end
