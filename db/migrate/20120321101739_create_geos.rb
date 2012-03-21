class CreateGeos < ActiveRecord::Migration
  def change
    create_table :geos do |t|
      t.string :address
      t.float :latitude
      t.float :longitude

      t.timestamps
    end
  end
end
