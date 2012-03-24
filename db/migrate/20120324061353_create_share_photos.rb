class CreateSharePhotos < ActiveRecord::Migration
  def change
    create_table :share_photos do |t|
      t.boolean :share, :default => false
      t.integer :photo_id

      t.timestamps
    end
  end
end
