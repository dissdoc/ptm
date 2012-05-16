class CreateAssortments < ActiveRecord::Migration
  def change
    create_table :assortments do |t|
      t.integer :user_id
      t.integer :photo_id

      t.timestamps
    end
  end
end
