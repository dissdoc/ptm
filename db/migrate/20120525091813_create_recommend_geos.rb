class CreateRecommendGeos < ActiveRecord::Migration
  def change
    create_table :recommend_geos do |t|
      t.string :comment
      t.string :address
      t.float :latitude
      t.float :longitude
      t.integer :user_id
      t.integer :geo_id

      t.timestamps
    end
  end
end
