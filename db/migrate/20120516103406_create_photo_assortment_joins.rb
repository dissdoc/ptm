class CreatePhotoAssortmentJoins < ActiveRecord::Migration
  def change
    create_table :photo_assortment_joins do |t|
      t.integer :photo_id
      t.integer :assortment_id

      t.timestamps
    end
  end
end
